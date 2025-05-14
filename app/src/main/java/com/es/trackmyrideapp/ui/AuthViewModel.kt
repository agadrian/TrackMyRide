package com.es.trackmyrideapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.RegisterUseCase
import com.es.trackmyrideapp.domain.usecase.SendPasswordResetUseCase
import com.es.trackmyrideapp.domain.usecase.SignInUseCase
import com.es.trackmyrideapp.ui.screens.forgotPasswordScreen.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginUiState
import com.es.trackmyrideapp.ui.screens.registerScreen.RegisterUiState


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    // UI States
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    private val _forgotPasswordUiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val forgotPasswordUiState: StateFlow<ForgotPasswordUiState> = _forgotPasswordUiState

    // Estado de error
    private val _showErrorMessage = MutableStateFlow<String?>(null)
    val showErrorMessage: StateFlow<String?> = _showErrorMessage

    // LOGIN
    fun signIn(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            val result = signInUseCase(email, password)

            result.fold(
                onSuccess = { authResult  ->
                    if (rememberMe) rememberMePreferences.setRememberMe(true)

                    val user = authResult.apiUser
                    val jwt = authResult.apiUser.jwtToken
                    authPreferences.setJwtToken(jwt)

                    _loginUiState.value = LoginUiState.Success(user, jwt)
                },
                onFailure = { exception ->
                    _loginUiState.value = LoginUiState.Idle
                    _showErrorMessage.value = exception.message ?: "Unknown error. Try Again Later"
                }
            )
        }
    }

    // REGISTER
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.Loading
            val result = registerUseCase(email, password)
            _registerUiState.value = RegisterUiState.Idle

            result.fold(
                onSuccess = { authResult ->
                    val user = authResult.apiUser
                    val jwt = authResult.apiUser.jwtToken
                    authPreferences.setJwtToken(jwt)

                    _registerUiState.value = RegisterUiState.Success(user, jwt)
                },
                onFailure = { exception ->
                    _showErrorMessage.value = exception.message ?: "Unknown error during registration"
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
                    _showErrorMessage.value = exception.message ?: "Unknown error during reseting your password"
                }
            )
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordUiState.value = ForgotPasswordUiState.Idle
    }

    fun consumeErrorMessage() {
        _showErrorMessage.value = null
    }
}
