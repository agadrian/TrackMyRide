package com.es.trackmyrideapp.domain.tracker


import android.util.Log
import com.es.trackmyrideapp.core.extensions.round
import com.es.trackmyrideapp.domain.model.RouteStats
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
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
        Log.d("Tracking", "Timer detenido. ${_elapsedTime.value}")
        endTimeMillis = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = null
        Log.d("Tracking", "Timer detenido final. ${_elapsedTime.value}")
    }

    fun getElapsedTimeMillis(): Long {
        val now = System.currentTimeMillis()
        val elapsed = if (endTimeMillis > 0) endTimeMillis - startTimeMillis
        else now - startTimeMillis

        Log.d("Tracking", """
        startTimeMillis: $startTimeMillis (${Date(startTimeMillis)})
        ${if (endTimeMillis > 0) "endTimeMillis: $endTimeMillis (${Date(endTimeMillis)})" else "now: $now (${Date(now)})"}
        elapsed: $elapsed ms
    """.trimIndent())

        return elapsed
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

    fun reset() {
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
                }else{
                    Log.d("Tracking", "No se puede calcular la velocidad max. Distancia: $distance, tiempo: $timeDeltaHours")
                }
            }else{
                Log.d("Tracking", "No se puede calcular la velocidad max. Tiempo delta: $timeDeltaHours")
            }
        }

        previousLocation = newLocation
        previousTime = currentTime
    }


    fun getCalculatedStats(points: List<LatLng>, efficiency: Double?): RouteStats {
        val elapsed = getElapsedTimeMillis()
        Log.d("Tracking", "getCalculatedStats: ${elapsed}")
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
