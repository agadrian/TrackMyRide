package com.es.trackmyrideapp.ui.screens.registerScreen

import com.es.trackmyrideapp.domain.model.User

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: User) : RegisterUiState()
}