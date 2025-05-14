package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.VehicleApi
import com.es.trackmyrideapp.data.remote.dto.VehicleUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.Vehicle
import com.es.trackmyrideapp.domain.repository.VehicleRepository
import com.es.trackmyrideapp.utils.safeApiCall
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {

    override suspend fun createInitialVehicles(): Resource<List<Vehicle>> = safeApiCall {
        vehicleApi.createInitialVehicles().map { it.toDomainModel() }
    }

    override suspend fun getAllVehicles(): Resource<List<Vehicle>> = safeApiCall {
        vehicleApi.getAllVehicles().map { it.toDomainModel() }
    }

    override suspend fun getVehicleByType(type: String): Resource<Vehicle> = safeApiCall {
        vehicleApi.getVehicleByType(type).toDomainModel()
    }

    override suspend fun updateVehicle(type: String, updateDTO: VehicleUpdateDTO): Resource<Vehicle> = safeApiCall {
        vehicleApi.updateVehicle(type, updateDTO).toDomainModel()
    }
}