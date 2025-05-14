package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.VehicleUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Vehicle

interface VehicleRepository {
    suspend fun createInitialVehicles(): Resource<List<Vehicle>>
    suspend fun getVehicleByType(type: String): Resource<Vehicle>
    suspend fun getAllVehicles(): Resource<List<Vehicle>>
    suspend fun updateVehicle(type: String, updateDTO: VehicleUpdateDTO): Resource<Vehicle>
}