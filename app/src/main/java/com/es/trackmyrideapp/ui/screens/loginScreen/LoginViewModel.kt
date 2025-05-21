package com.es.trackmyrideapp.ui.screens.loginScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.SendPasswordResetUseCase
import com.es.trackmyrideapp.domain.usecase.SignInUseCase
import com.es.trackmyrideapp.ui.screens.forgotPasswordScreen.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    // Estados del formulario
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var rememberMe by mutableStateOf(false)
        private set

    // UI State
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _forgotPasswordUiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val forgotPasswordUiState: StateFlow<ForgotPasswordUiState> = _forgotPasswordUiState

    // Funciones para actualizar los estados
    fun updateEmail(newEmail: String) { email = newEmail }
    fun updatePassword(newPassword: String) { password = newPassword }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun toggleRememberMe() { rememberMe = !rememberMe }

    // LOGIN
    fun signIn() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = signInUseCase(email, password)

            Log.d("FlujoTest", "authviewmodel: signInUseCase llamado")

            result.fold(
                onSuccess = { authResult  ->
                    if (rememberMe) rememberMePreferences.setRememberMe(true)

                    Log.d("FlujoTest", "authviewmodel: signInUseCase onsucces")

                    val user = authResult.apiUser
                    val jwt = authResult.apiUser.jwtToken
                    authPreferences.setJwtToken(jwt)
                    authPreferences.setRefreshToken(user.refreshToken)

                    val role = authPreferences.getUserRoleFromToken()

                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado osucces. user uid: ${user.uid} jwt: ${jwt}")
                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onsucces.setJwtToken y setrefresh llamados ")

                    Log.d("JWT Token", "Token recuperado usando getjwttoken(): ${authPreferences.getJwtToken()}")

                    _uiState.value = LoginUiState.Success(user, jwt, role)

                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onsucces. loginuiState: ${_uiState.value} ")

                    Log.d("JWT Token", "Login uisate: ${_uiState.value}")
                },
                onFailure = { exception ->
                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onfailure. exception: ${exception.message} ")
                    _uiState.value = LoginUiState.Idle
                    _errorMessage.value = exception.message ?: "Unknown error. Try Again Later"
                }
            )
        }
    }

    //  FORGOT PASSWORD
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _forgotPasswordUiState.value = ForgotPasswordUiState.Loading

            val result = sendPasswordResetUseCase(email)

            result.fold(
                onSuccess = {
                    _forgotPasswordUiState.value = ForgotPasswordUiState.Success
                },
                onFailure = { exception ->
                    _forgotPasswordUiState.value = ForgotPasswordUiState.Idle
                    _errorMessage.value = exception.message ?: "Unknown error during reseting your password"
                }
            )
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordUiState.value = ForgotPasswordUiState.Idle
    }

    fun consumeErrorMessage() {
        _errorMessage.value = null
    }
}