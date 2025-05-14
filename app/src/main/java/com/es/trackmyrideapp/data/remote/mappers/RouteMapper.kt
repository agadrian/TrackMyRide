package com.es.trackmyrideapp.data.remote.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.es.trackmyrideapp.data.remote.dto.RouteDTO
import com.es.trackmyrideapp.domain.model.Route
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
fun RouteDTO.toDomainModel(): Route {
    return Route(
        id = id,
        name = name,
        description = description,
        startTime = LocalDateTime.parse(startTime),
        endTime = LocalDateTime.parse(endTime),
        startPoint = startPoint,
        endPoint = endPoint,
        distanceKm = distanceKm,
        movingTimeSec = movingTimeSec,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        fuelConsumed = fuelConsumed,
        efficiency = efficiency,
        pace = pace,
        vehicleId = vehicleId,
        userId = userId
    )
}