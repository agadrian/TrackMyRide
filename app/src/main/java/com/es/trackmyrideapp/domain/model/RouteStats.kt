package com.es.trackmyrideapp.domain.model

import com.es.trackmyrideapp.core.extensions.msToFormattedTime

data class RouteStats(
    val elapsedTimeMillis: Long,
    val distanceMeters: Double,
    val averageSpeedKmh: Double,
    val fuelConsumed: Double?,
    val efficiency: Double?,
    val maxSpeed: Double,
    val paceSecondsPerKm: Double?
){
    override fun toString(): String {
        return """
        RouteStats:
        - Tiempo transcurrido: ${elapsedTimeMillis.msToFormattedTime()}
        - Distancia: ${"%.2f".format(distanceMeters/1000)} km
        - Velocidad promedio: ${"%.1f".format(averageSpeedKmh)} km/h
        - Velocidad máxima: ${"%.1f".format(maxSpeed)} km/h
        - Combustible consumido: ${fuelConsumed?.let { "%.2f L".format(it) } ?: "N/A"}
        - Eficiencia: ${efficiency?.let { "%.2f km/L".format(it) } ?: "N/A"}
        - Ritmo: ${paceSecondsPerKm?.let { "${it.toInt()} min/km" } ?: "N/A"}
        """.trimIndent()
    }
}