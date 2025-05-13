package com.es.trackmyrideapp.ui.screens.registerScreen

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.domain.usecase.RegisterUseCase
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _showErrorMessage = MutableStateFlow<String?>(null)
    val showErrorMessage: StateFlow<String?> = _showErrorMessage

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            val result = registerUseCase(email, password)
            _uiState.value = RegisterUiState.Idle

            if (result.isSuccess) {
                _uiState.value = RegisterUiState.Success(result.getOrThrow())
            } else {
                _showErrorMessage.value = result.exceptionOrNull()?.message ?: "Uknown Error @AdriAG"
            }
        }
    }

    fun consumeErrorMessage() {
        _showErrorMessage.value = null
    }
}