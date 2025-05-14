package com.es.trackmyrideapp.data.remote.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.es.trackmyrideapp.data.remote.dto.RouteDTO
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.ui.components.VehicleType
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RouteMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromDto(dto: RouteDTO): Route {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val start = LocalDateTime.parse(dto.startTime, formatter)
        val end = LocalDateTime.parse(dto.endTime, formatter)
        val duration = Duration.between(start, end).toMillis()

        return Route(
            id = dto.id.toInt(),
            name = dto.name,
            vehicleType = mapVehicleIdToType(dto.vehicleId),
            date = start.toLocalDate().toString(),
            distance = "${dto.distanceKm} km",
            pace = dto.pace?.let { "$it min/km" } ?: "-",
            duration = formatDuration(duration)
        )
    }

    fun toDto(route: Route, vehicleId: Long, userId: String, compressedPath: String): RouteDTO {
        return RouteDTO(
            id = route.id.toLong(),
            name = route.name,
            description = null, // Puedes extender tu modelo de dominio si quieres soportar esto
            startPoint = "Origen", // Placeholder si no tienes este dato en tu modelo
            endPoint = "Destino",
            startTime = "2025-05-09T10:15:30", // Debe venir de una capa superior (no hardcodeado)
            endTime = "2025-05-09T10:55:30",
            distanceKm = route.distance.removeSuffix(" km").toDouble(),
            movingTimeSec = 0L, // Igual, debes calcularlo antes de llamar al mapper
            avgSpeed = 0.0,
            maxSpeed = 0.0,
            fuelConsumed = null,
            efficiency = null,
            pace = route.pace.removeSuffix(" min/km").toDoubleOrNull(),
            vehicleId = vehicleId,
            userId = userId,
        )
    }

    private fun formatDuration(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun mapVehicleIdToType(vehicleId: Long): VehicleType {
        return when (vehicleId) {
            1L -> VehicleType.Car
            2L -> VehicleType.MotorCycle
            3L -> VehicleType.Bike
            else -> VehicleType.Car
        }
    }

    fun mapVehicleTypeToId(vehicleType: VehicleType): Long {
        return when (vehicleType) {
            VehicleType.Car -> 1L
            VehicleType.MotorCycle -> 2L
            VehicleType.Bike -> 3L
        }
    }

    fun RouteDTO.toDomainModel(): Route {
        return Route(
            id = this.id.toInt(),
            name = this.name,
            vehicleType = VehicleType.Car,
            date = this.startTime,
            distance = this.distanceKm.toString(),
            pace = this.pace.toString(),
            duration = this.movingTimeSec.toString()
        )
    }
}