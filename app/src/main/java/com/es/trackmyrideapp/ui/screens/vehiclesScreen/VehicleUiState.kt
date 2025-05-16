package com.es.trackmyrideapp.ui.screens.vehiclesScreen

sealed class VehicleUiState {
    object Idle : VehicleUiState()
    object Loading : VehicleUiState()
    object Success : VehicleUiState()
    data class Error(val message: String) : VehicleUiState()
}