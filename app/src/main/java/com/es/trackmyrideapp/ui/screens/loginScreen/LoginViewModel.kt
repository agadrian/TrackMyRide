package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.SendPasswordResetUseCase
import com.es.trackmyrideapp.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _showErrorMessage = MutableStateFlow<String?>(null)
    val showErrorMessage: StateFlow<String?> = _showErrorMessage

    fun signIn(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = signInUseCase(email, password)

            result.fold(
                onSuccess = { user ->
                    if (rememberMe) rememberMePreferences.setRememberMe(true)
                    _uiState.value = LoginUiState.Success(user)
                },
                onFailure = { exception ->
                    _uiState.value = LoginUiState.Idle
                    _showErrorMessage.value = exception.message ?: "Unknown error. Try Again Later"
                }
            )
        }
    }

    fun consumeErrorMessage() {
        _showErrorMessage.value = null
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = sendPasswordResetUseCase(email)
            result.onSuccess {
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

}