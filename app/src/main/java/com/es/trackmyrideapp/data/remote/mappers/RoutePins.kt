package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.RoutePinResponseDTO
import com.es.trackmyrideapp.domain.model.RoutePin

fun RoutePinResponseDTO.toDomainModel(): RoutePin {
    return RoutePin(
        id = this.id,
        latitude = this.latitude,
        longitude = this.longitude,
        title = this.title,
        description = this.description,
        routeId = this.routeId
    )
}