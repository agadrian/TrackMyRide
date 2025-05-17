package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.ui.components.VehicleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor() : SessionRepository {
    private val _selectedVehicle = MutableStateFlow<VehicleType>(VehicleType.CAR)
    override val selectedVehicle: StateFlow<VehicleType> = _selectedVehicle.asStateFlow()

    override fun setSelectedVehicle(vehicle: VehicleType) {
        _selectedVehicle.value = vehicle
    }
}


interface SessionRepository {
    val selectedVehicle: StateFlow<VehicleType>
    fun setSelectedVehicle(vehicle: VehicleType)
}