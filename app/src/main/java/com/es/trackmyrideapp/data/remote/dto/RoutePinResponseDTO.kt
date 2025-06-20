package com.es.trackmyrideapp.data.remote.dto

data class RoutePinResponseDTO(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null,
    val routeId: Long
)