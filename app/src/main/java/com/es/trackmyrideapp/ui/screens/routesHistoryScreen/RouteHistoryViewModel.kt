package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.usecase.routes.GetRoutesByUserUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.GetAllVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesHistoryViewModel @Inject constructor(
    private val getRoutesByUserUseCase: GetRoutesByUserUseCase,
    private val getAllVehiclesUseCase: GetAllVehiclesUseCase
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteWithVehicleType>>(emptyList())
    val routes: StateFlow<List<RouteWithVehicleType>> = _routes

    fun loadRoutes(userId: String) {
        viewModelScope.launch {
            val routesResult = getRoutesByUserUseCase(userId)
            val vehiclesResult = getAllVehiclesUseCase()

            if (routesResult is Resource.Success && vehiclesResult is Resource.Success) {
                val vehicleMap = vehiclesResult.data.associateBy { it.id }

                val enrichedRoutes = routesResult.data.mapNotNull { route ->
                    vehicleMap[route.vehicleId]?.let { vehicle ->
                        RouteWithVehicleType(route, vehicle.type)
                    }
                }

                _routes.value = enrichedRoutes
            } else {
                // TODO: Manejo de errores
            }
        }
    }
}