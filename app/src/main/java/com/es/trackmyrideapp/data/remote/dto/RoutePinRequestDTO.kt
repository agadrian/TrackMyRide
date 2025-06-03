package com.es.trackmyrideapp.data.remote.dto

data class RoutePinRequestDTO(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null,
    val routeId: Long
)