package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import com.es.trackmyrideapp.domain.model.RouteWithVehicleType

sealed class RoutesHistoryUiState {
    object Idle : RoutesHistoryUiState()
    object Loading : RoutesHistoryUiState()
    data class Success(val routes: List<RouteWithVehicleType>) : RoutesHistoryUiState()
    data class Error(val message: String) : RoutesHistoryUiState()
}