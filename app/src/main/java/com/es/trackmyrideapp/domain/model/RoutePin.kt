package com.es.trackmyrideapp.domain.model

class RoutePin(
    val id: Long? = null,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String?,
    val routeId: Long
)