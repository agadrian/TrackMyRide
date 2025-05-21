package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.RouteImageResponse
import com.es.trackmyrideapp.domain.model.RouteImage

fun RouteImageResponse.toDomainModel(): RouteImage {
    return RouteImage(
        id = id,
        routeId = routeId,
        imageUrl = imageUrl,
        description = description
    )
}