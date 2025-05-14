package com.es.trackmyrideapp.domain.usecase.vehicles

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Vehicle
import com.es.trackmyrideapp.domain.repository.VehicleRepository
import javax.inject.Inject


class GetAllVehiclesUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(): Resource<List<Vehicle>> {
        return vehicleRepository.getAllVehicles()
    }
}