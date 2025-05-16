package com.es.trackmyrideapp.domain.model

import com.es.trackmyrideapp.ui.components.VehicleType

data class Vehicle(
    val id: Long,
    val name: String,
    val brand: String,
    val model: String,
    val year:String?,
    val type: VehicleType,
    val fuelType: String?,
    val tankCapacity: Double?,
    val efficiency: Double?,
    val notes: String?
)