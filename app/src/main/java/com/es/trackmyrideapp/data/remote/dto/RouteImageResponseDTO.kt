package com.es.trackmyrideapp.data.remote.dto

data class RouteImageResponse(
    val id: Long,
    val routeId: Long,
    val imageUrl: String,
    val description: String?
)