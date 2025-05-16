package com.es.trackmyrideapp.domain.usecase.vehicles

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Vehicle
import com.es.trackmyrideapp.domain.repository.VehicleRepository
import com.es.trackmyrideapp.ui.components.VehicleType
import javax.inject.Inject


class GetVehicleByTypeUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(type: VehicleType): Resource<Vehicle> {
        return vehicleRepository.getVehicleByType(type)
    }
}