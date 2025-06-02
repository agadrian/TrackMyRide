package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel(

) : ILoginViewModel {
    // Sobrescribimos las variables para controlarlas manualmente
    override val emailError: MutableState<String?> = mutableStateOf(null)
    override val passwordError: MutableState<String?> = mutableStateOf(null)
    override val attemptedSubmit: MutableState<Boolean> = mutableStateOf(false)

    override var email by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordVisible by mutableStateOf(false)
    override var rememberMe by mutableStateOf(false)
    var role: String? = null

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val uiState: StateFlow<LoginUiState> = _uiState

    private val _errorMessage = MutableStateFlow<UiMessage?>(null)
    override val uiMessage: StateFlow<UiMessage?> = _errorMessage


    override fun updateEmail(newEmail: String) { email = newEmail }
    override fun updatePassword(newPassword: String) { password = newPassword }
    override fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    override fun toggleRememberMe() { rememberMe = !rememberMe }

    override fun signIn() {
        _uiState.value = LoginUiState.Loading

        if (email == "admin@admin.com" && password == "adminpass") {
            role = "ADMIN"
            _uiState.value = LoginUiState.Success(role)
            _errorMessage.value = null
        } else if (email.isNotEmpty() && password.isNotEmpty()) {
            role = "USER"
            _uiState.value = LoginUiState.Success(role)
            _errorMessage.value = null
        } else {
            role = null
            _uiState.value = LoginUiState.Idle
            _errorMessage.value = UiMessage("Login failed", MessageType.ERROR)
        }
    }

    override fun consumeUiMessage() {
        _errorMessage.value = null
    }

    // Métodos de simulación de estado si quieres (opcionales)
    fun simulateLoginSuccess(role: String) {
        _uiState.value = LoginUiState.Success(role)
        _errorMessage.value = null
    }

    fun simulateLoginLoading() {
        _uiState.value = LoginUiState.Loading
    }

    fun simulateLoginError(message: String) {
        _uiState.value = LoginUiState.Idle
        _errorMessage.value = UiMessage(message, MessageType.ERROR)
    }
}


