package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.ui.components.VehicleType
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val selectedVehicle: StateFlow<VehicleType>
    fun setSelectedVehicle(vehicle: VehicleType)
}