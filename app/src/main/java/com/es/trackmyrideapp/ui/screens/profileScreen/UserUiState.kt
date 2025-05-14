package com.es.trackmyrideapp.ui.screens.profileScreen

import com.es.trackmyrideapp.domain.model.User

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
    object Idle : UserUiState()
}
