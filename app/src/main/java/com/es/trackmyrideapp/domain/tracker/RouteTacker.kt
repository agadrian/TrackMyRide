package com.es.trackmyrideapp.domain.tracker


import android.util.Log
import com.es.trackmyrideapp.HomeScreenConstants.MAX_REASONABLE_SPEED_KMH
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

    private var _currentSpeedFlow = MutableStateFlow(0.0)
    val currentSpeedFlow: StateFlow<Double> = _currentSpeedFlow.asStateFlow()



    fun startTimer(scope: CoroutineScope) {
        startTimeMillis = System.currentTimeMillis()
        _elapsedTime.value = 0
        Log.d("Tracking", "Timer iniciado en: $startTimeMillis (${Date(startTimeMillis)})")


        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                _elapsedTime.value = System.currentTimeMillis() - startTimeMillis
                Log.d("Tracking", "Timer update - elapsed: ${_elapsedTime.value} ms")
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        Log.d("Tracking", "Deteniendo timer. Elapsed: ${_elapsedTime.value} ms")
        endTimeMillis = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = null
        Log.d("Tracking", "Timer detenido en: $endTimeMillis (${Date(endTimeMillis)}), total elapsed: ${_elapsedTime.value} ms")
    }


    fun getElapsedTimeMillis(): Long {
        val now = System.currentTimeMillis()
        val elapsed = if (endTimeMillis > 0) endTimeMillis - startTimeMillis
        else now - startTimeMillis

        Log.d("Tracking", """
            Cálculo elapsedTime:
            startTimeMillis: $startTimeMillis (${Date(startTimeMillis)})
            ${if (endTimeMillis > 0) "endTimeMillis: $endTimeMillis (${Date(endTimeMillis)})" else "now: $now (${Date(now)})"}
            elapsed: $elapsed ms
        """.trimIndent())

        return elapsed
    }

    fun getDistanceMeters(points: List<LatLng>): Double {
        var total = 0.0
        for (i in 1 until points.size) {
            val dist = distanceBetween(points[i - 1], points[i])
            total += dist
            //Log.d("Tracking", "Distancia entre punto $i-1 y $i: $dist metros")
        }
        //Log.d("Tracking", "Distancia total recorrida: $total metros")
        return total
    }


    private fun distanceBetween(p1: LatLng, p2: LatLng): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLng = Math.toRadians(p2.longitude - p1.longitude)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        Log.d("Tracking", "Calculando distancia entre puntos: $distance metros")
        return distance
    }

    fun getAverageSpeedKmh(points: List<LatLng>): Double {
        val hours = getElapsedTimeMillis() / 1000.0 / 3600.0
        val avgSpeed = if (hours > 0) getDistanceMeters(points) / 1000.0 / hours else 0.0
        Log.d("Tracking", "Velocidad promedio calculada: $avgSpeed km/h")
        return avgSpeed
    }


    fun reset() {
        Log.d("Tracking", "Resetear estado del tracker")
        _elapsedTime.value = 0
        startTimeMillis = 0
        endTimeMillis = 0
        maxSpeedKmh = 0.0
        _currentSpeedFlow.value = 0.0
        previousLocation = null
        previousTime = 0
        timerJob?.cancel()
        timerJob = null
    }


    fun updateLocation(newLocation: LatLng) {
        val currentTime = System.currentTimeMillis()
        Log.d("Tracking", "Update location: $newLocation en ${Date(currentTime)}")

        if (previousLocation != null && previousTime != 0L) {
            val distance = distanceBetween(previousLocation!!, newLocation)
            val timeDeltaMillis = currentTime - previousTime

            // Si esta quieto mas de dos segudnos, poner velocidad a 0
            if (distance < 0.4 && timeDeltaMillis > 2000){
                _currentSpeedFlow.value = 0.0
                Log.d("Tracking", "Velocidad puesta a 0 por inmovilidad.")
            }else{
                val timeDeltaHours = (currentTime - previousTime) / 1000.0 / 3600.0
                Log.d("Tracking", "Distancia desde la ubicación anterior: $distance metros, tiempo transcurrido: $timeDeltaHours horas")

                if (timeDeltaHours > 0) {
                    val speedKmh = (distance / 1000.0) / timeDeltaHours

                    if (speedKmh > MAX_REASONABLE_SPEED_KMH) {
                        Log.w("Tracking", "Velocidad irreal detectada: $speedKmh km/h. Ignorando punto.")
                        return
                    }

                    _currentSpeedFlow.value = speedKmh

                    Log.d("Tracking", "Velocidad calculada: $speedKmh km/h")
                    if (speedKmh > maxSpeedKmh) {
                        Log.d("Tracking", "Nueva velocidad máxima registrada: $speedKmh km/h (antes $maxSpeedKmh km/h)")
                        maxSpeedKmh = speedKmh
                    }else {
                        Log.d("Tracking", "Tiempo delta no es válido para calcular velocidad máxima.")
                    }
                } else {
                    Log.d("Tracking", "No hay ubicación o tiempo previo para calcular velocidad máxima.")
                }
            }
        }
        previousLocation = newLocation
        previousTime = currentTime
    }


    fun getCalculatedStats(points: List<LatLng>, efficiency: Double?): RouteStats {
        val elapsed = getElapsedTimeMillis()
        Log.d("Tracking", "Calculando estadísticas con elapsed time: $elapsed ms")
        val distance = getDistanceMeters(points)

        val distanceKm = distance / 1000.0

        val fuelConsumed = if (efficiency != null && efficiency > 0) {
            distanceKm / efficiency
        } else null

        val pace = if (distanceKm > 0) {
            (elapsed / 1000.0 / distanceKm) / 60.0
        } else null

        val avgSpeed = getAverageSpeedKmh(points)

        Log.d("\n\nTracking", """
            Stats calculados:
            distanceMeters=$distance,
            averageSpeedKmh=$avgSpeed,
            fuelConsumed=$fuelConsumed,
            efficiency=$efficiency,
            paceSecondsPerKm=$pace,
            maxSpeed=$maxSpeedKmh
        """.trimIndent())

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
