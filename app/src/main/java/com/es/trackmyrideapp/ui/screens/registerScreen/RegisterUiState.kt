package com.es.trackmyrideapp.ui.screens.registerScreen

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val role: String?) : RegisterUiState()
}