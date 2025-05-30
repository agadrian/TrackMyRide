package com.es.trackmyrideapp.core.states

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
}