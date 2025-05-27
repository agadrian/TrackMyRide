package com.es.trackmyrideapp.ui.screens.registerScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // Estados del formulario
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var password2 by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var password2Visible by mutableStateOf(false)
        private set

    // UI State
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Funciones para actualizar los estados
    fun updateEmail(newEmail: String) { email = newEmail }
    fun updatePassword(newPassword: String) { password = newPassword }
    fun updatePassword2(newPassword: String) { password2 = newPassword }
    fun updateUsername(newUsername: String) { username = newUsername }
    fun updatePhone(newPhone: String) { phone = newPhone }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun togglePassword2Visibility() { password2Visible = !password2Visible }

    // REGISTER
    fun register() {
        viewModelScope.launch {
            Log.d("FlujoTest", "authviewmodel: register llamado ")
            _uiState.value = RegisterUiState.Loading

            val userData = UserRegistrationDTO(
                username = username,
                phone = phone
            )

            val result = registerUseCase(email, password, userData)
            _uiState.value = RegisterUiState.Idle

            Log.d("FlujoTest", "authviewmodel: register llamado.")

            result.fold(
                onSuccess = { authResult ->
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