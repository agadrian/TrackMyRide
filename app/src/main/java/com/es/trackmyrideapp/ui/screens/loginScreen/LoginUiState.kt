package com.es.trackmyrideapp.ui.screens.loginScreen

import com.es.trackmyrideapp.domain.model.User

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    //data class Error(val message: String) : LoginUiState()
}