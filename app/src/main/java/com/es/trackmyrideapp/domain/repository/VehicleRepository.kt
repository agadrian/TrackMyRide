package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.VehicleUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Vehicle
import com.es.trackmyrideapp.ui.components.VehicleType

interface VehicleRepository {
    suspend fun createInitialVehicles(): Resource<List<Vehicle>>
    suspend fun getVehicleByType(type: VehicleType): Resource<Vehicle>
    suspend fun getAllVehicles(): Resource<List<Vehicle>>
    suspend fun updateVehicle(type: VehicleType, updateDTO: VehicleUpdateDTO): Resource<Vehicle>
}