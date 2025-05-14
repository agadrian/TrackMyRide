package com.es.trackmyrideapp.data.remote.dto

import com.es.trackmyrideapp.ui.components.VehicleType

data class VehicleResponseDTO(
    val id: Long,
    val name: String,
    val brand: String,
    val model: String,
    val year: Int,
    val type: VehicleType,
    val fuelType: String?,
    val tankCapacity: Double?,
    val efficiency: Double?,
    val notes: String?
)