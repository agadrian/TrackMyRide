package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.usecase.routes.DeleteRouteUseCase
import com.es.trackmyrideapp.domain.usecase.routes.GetRoutesByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesHistoryViewModel @Inject constructor(
    private val getRoutesByUserUseCase: GetRoutesByUserUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoutesHistoryUiState>(RoutesHistoryUiState.Idle)
    val uiState: StateFlow<RoutesHistoryUiState> = _uiState

    private val _routes = MutableStateFlow<List<RouteWithVehicleType>>(emptyList())
    val routes: StateFlow<List<RouteWithVehicleType>> = _routes


    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage

    fun consumeUiMessage() {
        _uiMessage.value = null
    }


    init {
        fetchRoutes()
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            _uiState.value = RoutesHistoryUiState.Loading
            when (val result = getRoutesByUserUseCase()) {
                is Resource.Success -> {
                    val routesWithVehicle = result.data.map {
                        RouteWithVehicleType(it, it.vehicleType)
                    }
                    _routes.value = routesWithVehicle
                    _uiState.value = RoutesHistoryUiState.Success(routesWithVehicle)
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                    _uiState.value = RoutesHistoryUiState.Idle
                }
                Resource.Loading -> {} // No hacer nada aquí
            }
        }
    }

    fun deleteRoute(routeId: Long) {
        viewModelScope.launch {
            _uiState.value = RoutesHistoryUiState.Loading
            when (val result = deleteRouteUseCase(routeId)) {
                is Resource.Success -> {
                    _uiMessage.value = UiMessage("Route deleted successfully", MessageType.INFO)
                    fetchRoutes()
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                    _uiState.value = RoutesHistoryUiState.Idle
                }
                Resource.Loading -> {}
            }
        }
    }
}


