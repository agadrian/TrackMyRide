package com.es.trackmyrideapp.data.remote.dto

data class RouteResponseDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val startTime: String,
    val endTime: String,
    val startPoint: String,
    val endPoint: String,
    val distanceKm: Double,
    val movingTimeSec: Long,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val fuelConsumed: Double?,
    val efficiency: Double?,
    val pace: Double?,
    val vehicleId: Long,
    val userId: String
)