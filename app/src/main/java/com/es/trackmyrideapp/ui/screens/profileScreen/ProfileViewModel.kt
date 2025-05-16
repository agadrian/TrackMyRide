package com.es.trackmyrideapp.ui.screens.profileScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.usecase.users.GetUserByIdUseCase
import com.es.trackmyrideapp.domain.usecase.users.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    // Estados del formulario
    var email by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var displayUsername by mutableStateOf("")
        private set
    var memberSince by mutableStateOf("")
        private set

    // Estados para el diálogo de cambio de contraseña
    var showChangePasswordDialog by mutableStateOf(false)
        private set
    var currentPassword by mutableStateOf("")
        private set
    var newPassword by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set




    // UI State
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState

    // Mensaje de confirmacion
    private val _confirmationMessage = MutableStateFlow<String?>(null)
    val confirmationMessage: StateFlow<String?> = _confirmationMessage

    fun consumeConfirmationMessage() {
        _confirmationMessage.value = null
    }

    // Funciones para actualizar estados
    fun updateEmail(newEmail: String) { email = newEmail }
    fun updateUsername(newUsername: String) { username = newUsername }
    fun updatePhone(newPhone: String) { phone = newPhone }
    fun updatePassword(newPassword: String) { password = newPassword }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }

    // TODO: Dialogo contraseña
    fun openChangePasswordDialog() { showChangePasswordDialog = true }
    fun closeChangePasswordDialog() {
        showChangePasswordDialog = false
        currentPassword = ""
        newPassword = ""
        confirmPassword = ""
    }
    fun updateCurrentPassword(value: String) { currentPassword = value }
    fun updateNewPassword(value: String) { newPassword = value }
    fun updateConfirmPassword(value: String) { confirmPassword = value }

    init {
        loadUserProfile()
    }



    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = authPreferences.getUserIdFromToken() ?: run {
                _uiState.value = ProfileUiState.Error("Invalid user token")
                return@launch
            }

            when (val result = getUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    result.data.let { user ->
                        username = user.username.replaceFirstChar { it.uppercaseChar() }
                        email = user.email
                        phone = user.phone ?: ""
                        memberSince = formatMemberSince(user.createdAt)
                        _uiState.value = ProfileUiState.Success(user)
                    }
                }
                is Resource.Error -> {
                    Log.d("ProfileViewModel", "Error loading profile: ${result.message}")
                    _uiState.value = ProfileUiState.Error(result.message ?: "Error loading profile")
                }

                Resource.Loading -> TODO()
            }
        }
    }



    fun updateProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = authPreferences.getUserIdFromToken() ?: run {
                _uiState.value = ProfileUiState.Error("Invalid user token")
                return@launch
            }

            val updateData = UserUpdateDTO(
                username = username,
                phone = phone.ifBlank { null }
            )

            when (val result = updateUserUseCase(userId, updateData)) {
                is Resource.Success -> {
                    result.data.let { user ->
                        username = user.username
                        phone = user.phone ?: ""
                        _uiState.value = ProfileUiState.Success(user)

                        _confirmationMessage.value = "Profile updated successfully"
                    }
                }
                is Resource.Error -> {
                    _uiState.value = ProfileUiState.Error(result.message)
                }

                Resource.Loading -> TODO()
            }
        }
    }

    fun validateBeforeSave(): Boolean {
        return when {
            username.isBlank() -> {
                _uiState.value = ProfileUiState.Error("Username cannot be empty")
                false
            }
            phone.length > 12 -> {
                _uiState.value = ProfileUiState.Error("Phone number too long")
                false
            }
            else -> true
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


}

