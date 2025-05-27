package com.es.trackmyrideapp.ui.screens.adminScreen

import com.es.trackmyrideapp.domain.model.User

sealed class AdminUiState {
    object Idle : AdminUiState()
    object Loading : AdminUiState()
    data class Success(val users: List<User>) : AdminUiState()
}