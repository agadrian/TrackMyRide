package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.RouteResponseDTO
import com.es.trackmyrideapp.domain.model.Route


fun RouteResponseDTO.toDomainModel(): Route {
    return Route(
        id = id,
        name = name,
        description = description,
        startTime = startTime,
        endTime = endTime,
        startPoint = startPoint,
        endPoint = endPoint,
        distanceKm = distanceKm,
        movingTimeSec = movingTimeSec,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        fuelConsumed = fuelConsumed,
        efficiency = efficiency,
        pace = pace,
        vehicleType = vehicleType,
        userId = userId
    )
}