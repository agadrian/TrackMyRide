package com.es.trackmyrideapp.ui.screens.registerScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.ui.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authViewModel: AuthViewModel
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

    fun register() {
        if (password != password2) {
            _errorMessage.value = "Passwords are not the same"
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            authViewModel.register(
                email = email,
                password = password,
                userData = UserRegistrationDTO(
                    username = username,
                    phone = phone
                )
            )

            // Observamos el estado del AuthViewModel
            authViewModel.registerUiState.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun consumeErrorMessage() {
        _errorMessage.value = null
    }
}