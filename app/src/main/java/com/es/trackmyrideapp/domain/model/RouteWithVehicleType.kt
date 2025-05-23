package com.es.trackmyrideapp.domain.model

import com.es.trackmyrideapp.ui.components.VehicleType

data class RouteWithVehicleType(
    val route: Route,
    val vehicleType: VehicleType
)