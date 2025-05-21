package com.es.trackmyrideapp.domain.model

data class RouteImage(
    val id: Long,
    val routeId: Long,
    val imageUrl: String,
    val description: String?
)