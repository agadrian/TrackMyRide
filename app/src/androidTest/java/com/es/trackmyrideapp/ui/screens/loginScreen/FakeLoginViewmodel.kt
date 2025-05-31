package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.es.trackmyrideapp.ui.viewmodels.ISessionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLoginViewModel : ILoginViewModel {
    // Sobrescribimos las variables para controlarlas manualmente
    override var email by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordVisible by mutableStateOf(false)
    override var rememberMe by mutableStateOf(false)
    var role: String? = null

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val uiState: StateFlow<LoginUiState> = _uiState

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val uiMessage: StateFlow<String?> = _errorMessage


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
            _errorMessage.value = "Login failed"
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
        _errorMessage.value = message
    }
}


class FakeSessionViewModel : ISessionViewModel {

    // Estado mutable interno que mantiene el valor booleano
    private val _isLoading = mutableStateOf(false)

    // Propiedad pública inmutable que expone el estado
    override val isLoading: State<Boolean> = _isLoading

    override fun showLoading() {
        _isLoading.value = true
    }

    override fun hideLoading() {
        _isLoading.value = false
    }

    override fun onUserLoggedIn() {
        // Aquí puedes simular lo que necesites, o dejar vacío
    }
}