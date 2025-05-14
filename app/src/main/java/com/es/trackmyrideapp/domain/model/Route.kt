package com.es.trackmyrideapp.domain.model

import com.es.trackmyrideapp.ui.components.VehicleType

data class Route(
    val id: Int,
    val name: String,
    val vehicleType: VehicleType,
    val date: String,
    val distance: String,
    val pace: String,
    val duration: String
)