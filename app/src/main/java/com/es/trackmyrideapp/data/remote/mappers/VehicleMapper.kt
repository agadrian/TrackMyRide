package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.VehicleResponseDTO
import com.es.trackmyrideapp.domain.model.Vehicle

fun VehicleResponseDTO.toDomainModel(): Vehicle {
    return Vehicle(
        id = this.id,
        name = this.name,
        brand = this.brand,
        model = this.model,
        year = this.year,
        type = this.type,
        fuelType = this.fuelType,
        tankCapacity = this.tankCapacity,
        efficiency = this.efficiency,
        notes = this.notes
    )
}