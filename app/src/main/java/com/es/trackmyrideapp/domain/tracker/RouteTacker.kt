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
import kotlin.math.*

class RouteTracker {
    private var startTimeMillis: Long = 0
    private var endTimeMillis: Long = 0

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private var timerJob: Job? = null

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

    private fun distanceBetween(p1: LatLng, p2: LatLng): Double {
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
        timerJob?.cancel()
        timerJob = null
    }
}
