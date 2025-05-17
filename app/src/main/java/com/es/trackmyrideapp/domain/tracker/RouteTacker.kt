package com.es.trackmyrideapp.domain.tracker


import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class RouteTracker {
    var startTimeMillis: Long = 0
    private var endTimeMillis: Long = 0

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private var timerJob: Job? = null
    private var maxSpeedKmh = 0.0
    private var previousLocation: LatLng? = null
    private var previousTime: Long = 0


    fun startTimer(scope: CoroutineScope) {
        startTimeMillis = System.currentTimeMillis()
        _elapsedTime.value = 0

        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                _elapsedTime.value = System.currentTimeMillis() - startTimeMillis
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        endTimeMillis = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = null
        reset()
    }

    fun getElapsedTimeMillis(): Long {
        return if (endTimeMillis > 0) endTimeMillis - startTimeMillis
        else System.currentTimeMillis() - startTimeMillis
    }

    fun getDistanceMeters(points: List<LatLng>): Double {
        var total = 0.0
        for (i in 1 until points.size) {
            total += distanceBetween(points[i - 1], points[i])
        }
        return total
    }

    fun getAverageSpeedKmh(points: List<LatLng>): Double {
        val hours = getElapsedTimeMillis() / 1000.0 / 3600.0
        return if (hours > 0) getDistanceMeters(points) / 1000.0 / hours else 0.0
    }

    fun distanceBetween(p1: LatLng, p2: LatLng): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLng = Math.toRadians(p2.longitude - p1.longitude)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun reset() {
        _elapsedTime.value = 0
        startTimeMillis = 0
        endTimeMillis = 0
        maxSpeedKmh = 0.0
        previousLocation = null
        previousTime = 0
        timerJob?.cancel()
        timerJob = null
    }


    fun updateLocation(newLocation: LatLng) {
        val currentTime = System.currentTimeMillis()

        previousLocation?.let { prevLoc ->
            val distance = distanceBetween(prevLoc, newLocation)
            val timeDeltaHours = (currentTime - previousTime) / 1000.0 / 3600.0

            if (timeDeltaHours > 0) {
                val speedKmh = (distance / 1000.0) / timeDeltaHours
                if (speedKmh > maxSpeedKmh) {
                    maxSpeedKmh = speedKmh
                }
            }
        }

        previousLocation = newLocation
        previousTime = currentTime
    }


    fun getCalculatedStats(points: List<LatLng>, efficiency: Double?): RouteStats {
        val elapsed = getElapsedTimeMillis()
        val distance = getDistanceMeters(points)
        val avgSpeed = if (elapsed > 0) distance / (elapsed / 1000.0 / 3600.0) else 0.0
        val distanceKm = distance / 1000.0

        val fuelConsumed = if (efficiency != null && efficiency > 0) {
            distanceKm / efficiency
        } else null

        val pace = if (distanceKm > 0) {
            (elapsed / 1000.0 / distanceKm) / 60.0
        } else null

        return RouteStats(
            elapsedTimeMillis = elapsed,
            distanceMeters = distance.round(),
            averageSpeedKmh = avgSpeed.round(),
            fuelConsumed = fuelConsumed?.round(),
            efficiency = efficiency?.round(),
            paceSecondsPerKm = pace?.round(),
            maxSpeed = maxSpeedKmh
        )
    }


}
fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

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

fun Long.msToFormattedTime(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
}
