package com.es.trackmyrideapp.ui.screens.loginScreen

import com.es.trackmyrideapp.domain.model.AuthenticatedUser

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: AuthenticatedUser, val jwtToken: String?) : LoginUiState()
    //data class Error(val message: String) : LoginUiState()
}