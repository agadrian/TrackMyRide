package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.RoutesHistoryConstants
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RouteWithVehicleType
import com.es.trackmyrideapp.domain.usecase.routes.DeleteRouteUseCase
import com.es.trackmyrideapp.domain.usecase.routes.GetRoutesByUserUseCase
import com.es.trackmyrideapp.ui.components.VehicleFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesHistoryViewModel @Inject constructor(
    private val getRoutesByUserUseCase: GetRoutesByUserUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteWithVehicleType>>(emptyList())
    val routes: StateFlow<List<RouteWithVehicleType>> = _routes

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Ui Messages
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage

    fun consumeUiMessage() {
        _uiMessage.value = null
    }


    init {
        fetchRoutes()
    }

    fun getFilteredRoutes(isPremium: Boolean, filter: VehicleFilter): List<RouteWithVehicleType> {
        val filtered = when (filter) {
            is VehicleFilter.All -> _routes.value
            is VehicleFilter.Type -> _routes.value.filter {
                it.vehicleType == filter.type
            }
        }

        val maxVisible = if (isPremium) RoutesHistoryConstants.MAX_ROUTES_PREMIUM else RoutesHistoryConstants.MAX_ROUTES_NO_PREMIUM
        return filtered.take(maxVisible)
    }


    fun fetchRoutes() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = getRoutesByUserUseCase()) {
                is Resource.Success -> {
                    val routesWithVehicle = result.data.map {
                        RouteWithVehicleType(it, it.vehicleType)
                    }
                    _routes.value = routesWithVehicle
                    _uiState.value = UiState.Idle
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Error loading routes, please try again altger.", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun deleteRoute(routeId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = deleteRouteUseCase(routeId)) {
                is Resource.Success -> {
                    _uiMessage.value = UiMessage("Route deleted successfully", MessageType.INFO)
                    fetchRoutes()
                    _uiState.value = UiState.Idle
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Error deleting route, please try again later.", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun shouldShowGetPremiumButton(
        isPremium: Boolean,
        selectedFilter: VehicleFilter
    ): Boolean {
        val filtered = when (selectedFilter) {
            is VehicleFilter.All -> _routes.value
            is VehicleFilter.Type -> _routes.value.filter { it.vehicleType == selectedFilter.type }
        }

        return !isPremium && selectedFilter is VehicleFilter.All && filtered.count() > 4
    }
}


