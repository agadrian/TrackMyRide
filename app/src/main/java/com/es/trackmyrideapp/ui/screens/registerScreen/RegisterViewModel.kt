package com.es.trackmyrideapp.ui.screens.registerScreen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.RegisterScreenConstants.MAX_PHONE_LENGTH
import com.es.trackmyrideapp.RegisterScreenConstants.MAX_USERNAME_LENGTH
import com.es.trackmyrideapp.RegisterScreenConstants.MIN_PASSWORD_LENGTH
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.domain.usecase.RegisterUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.CreateInitialVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val createInitialVehiclesUseCase: CreateInitialVehiclesUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var password2 = mutableStateOf("")
        private set

    var username = mutableStateOf("")
        private set

    var phone = mutableStateOf("")
        private set

    var passwordVisible = mutableStateOf(false)
        private set

    var password2Visible = mutableStateOf(false)
        private set

    val emailError = mutableStateOf<String?>(null)

    val usernameError = mutableStateOf<String?>(null)

    val phoneError = mutableStateOf<String?>(null)

    val passwordError = mutableStateOf<String?>(null)

    val password2Error = mutableStateOf<String?>(null)

    val attemptedSubmit = mutableStateOf(false)


    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return "Email cannot be empty"
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return if (!pattern.matcher(value).matches()) "Invalid email address" else null
    }

    private fun validateUsername(value: String): String? {
        return when {
            value.isBlank() -> "Username cannot be empty"
            value.length > MAX_USERNAME_LENGTH -> "Max $MAX_USERNAME_LENGTH characters allowed"
            else -> null
        }
    }

    private fun validatePhone(value: String): String? {
        return if (value.length > MAX_PHONE_LENGTH) "Max $MAX_PHONE_LENGTH characters allowed" else null
    }

    private fun validatePassword(value: String): String? {
        return if (value.length < MIN_PASSWORD_LENGTH) "Minimum $MIN_PASSWORD_LENGTH characters required" else null
    }

    private fun validatePassword2(value: String, password: String): String? {
        return when {
            value.length < MIN_PASSWORD_LENGTH -> "Minimum $MIN_PASSWORD_LENGTH characters required"
            value != password -> "Passwords do not match"
            else -> null
        }
    }

    fun validateAll(): Boolean {
        emailError.value = validateEmail(email.value)
        usernameError.value = validateUsername(username.value)
        phoneError.value = validatePhone(phone.value)
        passwordError.value = validatePassword(password.value)
        password2Error.value = validatePassword2(password2.value, password.value)

        return listOf(
            emailError.value,
            usernameError.value,
            phoneError.value,
            passwordError.value,
            password2Error.value
        ).all { it == null }
    }


    // UI State
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Funciones para actualizar los estados
    fun updateEmail(newEmail: String) { email.value = newEmail }
    fun updatePassword(newPassword: String) { password.value = newPassword }
    fun updatePassword2(newPassword: String) { password2.value = newPassword }
    fun updateUsername(newUsername: String) { username.value= newUsername }
    fun updatePhone(newPhone: String) { phone.value = newPhone }
    fun togglePasswordVisibility() { passwordVisible.value = !passwordVisible.value }
    fun togglePassword2Visibility() { password2Visible.value = !password2Visible.value }

    // REGISTER
    fun register() {
        attemptedSubmit.value = true
        if (!validateAll()) return

        viewModelScope.launch {
            Log.d("FlujoTest", "authviewmodel: register llamado ")
            _uiState.value = RegisterUiState.Loading

            val userData = UserRegistrationDTO(
                username = username.value,
                phone = phone.value
            )

            val result = registerUseCase(email.value, password.value, userData)
            _uiState.value = RegisterUiState.Idle

            Log.d("FlujoTest", "authviewmodel: register llamado.")

            result.fold(
                onSuccess = {
                    val role = authPreferences.getUserRoleFromToken()
                    _uiState.value = RegisterUiState.Success(role)

                    if (role != "ADMIN"){
                        // Crear los vehiculos iniciales cuando no sea admin solo
                        launch {
                            val vehiclesResult = createInitialVehiclesUseCase()
                            Log.d("FlujoTest", "Registerviewmodel. Resultado de createInitialVehicles: $vehiclesResult")
                        }
                    }

                },
                onFailure = { exception ->
                    Log.d("FlujoTest", "authviewmodel: register llamado. onfailure. exception: ${exception.message} ")
                    _errorMessage.value = exception.message ?: "Unknown error during registration"
                }
            )
        }
    }

    fun consumeErrorMessage() {
        _errorMessage.value = null
    }
}