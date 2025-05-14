package com.es.trackmyrideapp.ui.screens.registerScreen

import com.es.trackmyrideapp.domain.model.AuthenticatedUser

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: AuthenticatedUser, val jwtToken: String?) : RegisterUiState()
}