package com.es.trackmyrideapp.ui.screens.profileScreen

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequest
import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.usecase.images.UploadImageToCloudinaryUseCase
import com.es.trackmyrideapp.domain.usecase.profileImages.GetProfileImageUseCase
import com.es.trackmyrideapp.domain.usecase.profileImages.UploadProfileImageUseCase
import com.es.trackmyrideapp.domain.usecase.users.GetUserByIdUseCase
import com.es.trackmyrideapp.domain.usecase.users.UpdateUserUseCase
import com.es.trackmyrideapp.utils.ApiErrorHandler
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val authPreferences: AuthPreferences,
    private val uploadImageToCloudinaryUseCase: UploadImageToCloudinaryUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val getProfileImageUseCase: GetProfileImageUseCase
) : ViewModel() {

    // Estados del formulario
    var email: MutableState<String> = mutableStateOf("")
        private set

    var username: MutableState<String> = mutableStateOf("")
        private set

    var savedUsername: MutableState<String> = mutableStateOf("")
        private set

    var usernameError: MutableState<String?> = mutableStateOf(null)
        private set

    var phone: MutableState<String> = mutableStateOf("")
        private set

    var phoneError: MutableState<String?> = mutableStateOf(null)
        private set

    var password: MutableState<String> = mutableStateOf("")
        private set

    var memberSince: MutableState<String> = mutableStateOf("")
        private set

    // Change password button. Passwordsa y errores
    val showChangePasswordDialog = MutableStateFlow(false)

    val currentPassword = MutableStateFlow("")
    val newPassword = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val currentPasswordVisible = MutableStateFlow(false)
    val newPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = MutableStateFlow(false)

    val currentPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = MutableStateFlow<String?>(null)

    val passwordDialogError = MutableStateFlow<String?>(null)

    fun toggleCurrentPasswordVisibility() {
        currentPasswordVisible.value = !currentPasswordVisible.value
    }

    fun toggleNewPasswordVisibility() {
        newPasswordVisible.value = !newPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible.value = !confirmPasswordVisible.value
    }

    fun resetPasswordDialogState() {
        currentPassword.value = ""
        newPassword.value = ""
        confirmPassword.value = ""
        currentPasswordVisible.value = false
        newPasswordVisible.value = false
        confirmPasswordVisible.value = false
        currentPasswordError.value = null
        newPasswordError.value = null
        confirmPasswordError.value = null
        passwordDialogError.value = null
        showChangePasswordDialog.value = false

    }


    fun openChangePasswordDialog() {
        showChangePasswordDialog.value = true
    }


    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Ui Messages
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage


    // Funciones para actualizar estados
    fun updateUsername(newUsername: String) {
        username.value = newUsername
        usernameError.value = validateUsername(newUsername)
    }

    private fun validateUsername(value: String): String? {
        return when {
            value.isBlank() -> "Username cannot be empty"
            value.length > 10 -> "Max 10 characters"
            else -> null
        }
    }

    fun updatePhone(newPhone: String) {
        phone.value = newPhone
        phoneError.value = validatePhone(newPhone)
    }

    private fun validatePhone(value: String): String? {
        return when {
            value.length > 9 -> "Max 9 characters"
            else -> null
        }
    }

    fun validateAll(): Boolean {
        usernameError.value = validateUsername(username.value)
        phoneError.value = validatePhone(phone.value)

        return usernameError.value == null && phoneError.value == null
    }


    init {
        loadUserProfile()
        fetchProfileImage()
    }


    private fun loadUserProfile() {
        Log.d("flujotest", "loaduserporofile llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val userId = authPreferences.getUserIdFromToken() ?: run {
                _uiMessage.value = UiMessage("Invalid user token", MessageType.ERROR)
                _uiState.value = UiState.Idle
                return@launch
            }

            when (val result = getUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    result.data.let { user ->
                        val formattedUsername =
                            user.username.replaceFirstChar { it.uppercaseChar() }
                        username.value = formattedUsername
                        email.value = user.email
                        phone.value = user.phone ?: ""
                        memberSince.value = formatMemberSince(user.createdAt)
                        savedUsername.value = formattedUsername
                        _uiState.value = UiState.Idle
                    }
                }

                is Resource.Error -> {
                    Log.d("ProfileViewModel", "Error loading profile: ${result.message}")
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }


    fun updateProfile() {
        Log.d("flujotest", "updatesuerprofile llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val userId = authPreferences.getUserIdFromToken() ?: run {
                _uiMessage.value = UiMessage("Invalid user token", MessageType.ERROR)
                _uiState.value = UiState.Idle
                return@launch
            }

            val updateData = UserUpdateDTO(
                username = username.value,
                phone = phone.value.ifBlank { null }
            )

            when (val result = updateUserUseCase(userId, updateData)) {
                is Resource.Success -> {
                    result.data.let { user ->
                        username.value = user.username
                        savedUsername.value = user.username
                        phone.value = user.phone ?: ""
                        _uiMessage.value = UiMessage("Profile updated successfully", MessageType.INFO)
                        _uiState.value = UiState.Idle
                    }
                }

                is Resource.Error -> {
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun validateBeforeSave(): Boolean {
        return when {
            username.value.isBlank() -> {
                _uiMessage.value = UiMessage("Username cannot be empty", MessageType.ERROR)
                false
            }

            username.value.length > 15 -> {
                _uiMessage.value = UiMessage("Username too long", MessageType.ERROR)
                false
            }

            phone.value.length > 12 -> {
                _uiMessage.value = UiMessage("Phone number too long", MessageType.ERROR)
                false
            }

            else -> true
        }
    }

    fun changePassword() {
        val current = currentPassword.value.trim()
        val new = newPassword.value.trim()
        val confirm = confirmPassword.value.trim()

        if (!validatePasswordFields(current, new, confirm)) return

        reauthenticateAndChangePassword(current, new)
    }

    private fun validatePasswordFields(current: String, new: String, confirm: String): Boolean {
        var valid = true

        currentPasswordError.value = if (current.isEmpty()) {
            valid = false
            "Enter current password"
        } else null

        newPasswordError.value = when {
            new.isEmpty() -> {
                valid = false
                "Enter new password"
            }

            new.length < 8 -> {
                valid = false
                "Must be at least 8 characters"
            }

            new == current -> {
                valid = false
                "Must be different from current"
            }

            else -> null
        }

        confirmPasswordError.value = when {
            confirm.isEmpty() -> {
                valid = false
                "Confirm your password"
            }

            confirm != new -> {
                valid = false
                "Passwords do not match"
            }

            else -> null
        }

        return valid
    }

    private fun reauthenticateAndChangePassword(current: String, new: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user == null || email == null) {
            passwordDialogError.value = "User not logged in"
            return
        }

        _uiState.value = UiState.Loading

        val credential = EmailAuthProvider.getCredential(email, current)
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    updatePassword(user, new)
                } else {
                    _uiState.value = UiState.Idle
                    passwordDialogError.value =
                        "Re-authentication failed. Check your current password."
                }
            }
    }

    private fun updatePassword(user: FirebaseUser, newPassword: String) {
        user.updatePassword(newPassword)
            .addOnCompleteListener { updateTask ->
                _uiState.value = UiState.Idle
                if (updateTask.isSuccessful) {
                    _uiMessage.value = UiMessage("Password updated successfully", MessageType.INFO)
                    resetPasswordDialogState()
                } else {
                    passwordDialogError.value =
                        updateTask.exception?.message ?: "Password update failed"
                }
            }
    }


    private fun formatMemberSince(date: Date): String {
        return try {
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /*  Imagenes perfil  */

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    fun uploadProfileImage(uri: Uri) {
        Log.d("flujotest", "uploasdprofileimage llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val imageUrl = uploadImageToCloudinaryUseCase(uri)
                if (imageUrl == null) {
                    _uiMessage.value = UiMessage("Failed to upload image to Cloudinary", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                    return@launch
                }

                val request = ProfileImageRequest(imageUrl = imageUrl)
                val result = uploadProfileImageUseCase(request)
                _uiState.value = UiState.Idle

                if (result is Resource.Success) {
                    fetchProfileImage()
                    _uiMessage.value = UiMessage("Profile picture updated", MessageType.INFO)
                } else if (result is Resource.Error) {
                    val message = ApiErrorHandler.handleApiError(
                        result
                    )
                    _uiMessage.value = message
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Idle
                _uiMessage.value = UiMessage("Error uploading image: ${e.localizedMessage}", MessageType.ERROR)
            }
        }
    }

    private fun fetchProfileImage() {
        Log.d("flujotest", "fetchprofileimage llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val result = getProfileImageUseCase()
            _uiState.value = UiState.Idle

            when (result) {
                is Resource.Success -> {
                    _profileImageUrl.value = result.data.imageUrl
                }
                is Resource.Error -> {
                    val message = ApiErrorHandler.handleApiError(
                        result,
                        customMessages = mapOf(
                            404 to ""
                        )
                    )
                    if (message.message.isNotEmpty()) {
                        _uiMessage.value = message
                    }
                }
            }
        }
    }


    fun consumeUiMessage() {
        _uiMessage.value = null
    }
}

