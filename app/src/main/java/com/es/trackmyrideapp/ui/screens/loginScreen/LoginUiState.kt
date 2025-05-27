package com.es.trackmyrideapp.ui.screens.loginScreen

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val role: String?) : LoginUiState()
}