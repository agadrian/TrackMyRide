package com.es.trackmyrideapp.ui.screens.profileScreen

import com.es.trackmyrideapp.domain.model.User

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}