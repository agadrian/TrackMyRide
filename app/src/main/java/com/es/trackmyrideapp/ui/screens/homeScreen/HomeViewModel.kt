package com.es.trackmyrideapp.ui.screens.homeScreen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.domain.tracker.RouteTracker
import com.es.trackmyrideapp.utils.GPXParser.parseWikilocGpx
import com.es.trackmyrideapp.utils.RouteCompress.compressRouteWithDelta
import com.es.trackmyrideapp.utils.RouteCompress.decompressRoute
import com.es.trackmyrideapp.utils.RouteSimplifier
import com.es.trackmyrideapp.utils.RouteSimplifier.generateRealisticSampleRoute
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    private val routeTracker = RouteTracker()

    private val _elapsedTime = mutableStateOf(0L)
    val elapsedTime: State<Long> = _elapsedTime

    private val _trackingState = mutableStateOf(false)
    val trackingState: State<Boolean> = _trackingState

    private var _routePoints = mutableStateOf<List<LatLng>>(emptyList())
    val routePoints: State<List<LatLng>> = _routePoints

    private val _routePointsSaved = mutableStateOf<List<LatLng>>(emptyList())
    val routePointsSaved: State<List<LatLng>> = _routePointsSaved

    private val _currentLocation = mutableStateOf<LatLng?>(null)
    val currentLocation: State<LatLng?> = _currentLocation

    private var locationCallback: LocationCallback? = null

    // Puntos gps acortados
    private val _simplifiedRoutePoints = mutableStateOf<List<LatLng>>(emptyList())
    val simplifiedRoutePoints: State<List<LatLng>> = _simplifiedRoutePoints

    private var _routePointsTest = mutableStateOf<List<LatLng>>(emptyList())
    val routePointsTest: State<List<LatLng>> = _routePointsTest


    /**
     * Comprimir ruta:
     * - 1º RouteSimplifier.simplify(points, 0.00005) -> Simplified
     * - 2º compressRouteWithDelta(simplifiedPoints) -> BinaryData
     * - 3º Base64.encodeToString(binaryData, Base64.NO_WRAP) -> Base 64
     */
    fun loadGpxRoute(context: Context) {
        viewModelScope.launch {
            val points = parseWikilocGpx(context, R.raw.miruta)
            val startPoint = points.firstOrNull()
            val endPoint = points.lastOrNull()

            val startStreet = startPoint?.let { getStreetAndNumber(context, it.latitude, it.longitude) }
            val endStreet = endPoint?.let { getStreetAndNumber(context, it.latitude, it.longitude) }

            val simpli = RouteSimplifier.simplify(points, 0.00005)
            val binaryData = compressRouteWithDelta(simpli)
            val base64ForApi = Base64.encodeToString(binaryData, Base64.NO_WRAP)
            _routePointsTest.value = simpli
            Log.d("Tracking", "Puntos cargados: ${points.size}. Puntos simplificados: ${simpli.size}. Puntos binarydata:  ${binaryData.size}. Base64: ${base64ForApi.length}. Start street: $startStreet. End street: $endStreet")
            Log.d("Tracking", "Base64: $base64ForApi")

            val binaryDecompressed = Base64.decode(base64ForApi, Base64.NO_WRAP)
            val pointsDecompressed = decompressRoute(binaryDecompressed)
            Log.d("Tracking", "Puntos descomprimidos: ${pointsDecompressed.size}")

        }
    }

    private fun getStreetAndNumber(context: Context, lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val address = addresses?.firstOrNull()
            if (address != null) {
                val street = address.thoroughfare ?: ""
                val number = address.subThoroughfare ?: ""
                if (street.isNotBlank() && number.isNotBlank()) {
                    "$street, $number"
                } else {
                    street.ifBlank { address.getAddressLine(0) } // fallback
                }
            } else null
        } catch (e: Exception) {
            Log.e("Geocoding", "Error getting street name and number: ${e.message}")
            null
        }
    }



    init {
        getLastKnownLocation()
        //generateMockRouteFromStartPoint()
        //generateSampleRoute()
    }



    override fun onCleared() {
        super.onCleared()
        // Limpiar las actualizaciones de ubicación cuando el ViewModel sea destruido
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
            Log.d("Tracking", "Ubicaciones detenidas, callback removido.")
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    _currentLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Tracking", "Error getting last location", e)
                // Si falla, intentamos obtener actualizaciones de ubicación
                requestLocationUpdatesForInitialPosition()
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdatesForInitialPosition() {
        // Usamos LocationRequest.Builder() para la nueva API
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).apply {
            setIntervalMillis(3000)  // El intervalo más rápido
            setMaxUpdates(1)  // Solo queremos una actualización para la posición inicial
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    _currentLocation.value = LatLng(location.latitude, location.longitude)
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()  // Usar el hilo principal
        )
    }


    fun toggleTracking() {
        if (_trackingState.value) {
            stopLocationUpdates()
        } else {
            startLocationUpdates()
        }
    }

    private fun saveCurrentRoute() {
        if (_routePoints.value.size > 1) {
            // TODO: Esto en el futuro en la BD
//            val route = Route(
//                points = _routePoints.value,
//                distance = calculateTotalDistance(),
//                timestamp = System.currentTimeMillis()
//            )
//            viewModelScope.launch {
//                routeRepository.saveRoute(route)
//            }
            _routePointsSaved.value = _routePoints.value
            Log.d("Tracking", "Ruta guardada: ${_routePointsSaved.value}")
           // simplifyCurrentRoute()
            val simplified = RouteSimplifier.simplify(_routePoints.value, tolerance =0.2)
            _simplifiedRoutePoints.value = simplified
        }
        clearRoute()
    }

    // Función para iniciar actualizaciones de ubicación
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).apply {

            setMinUpdateDistanceMeters(5f)  // Distancia mínima de 5 metros entre actualizaciones
            setWaitForAccurateLocation(true)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    updateRoutePoints(latLng)
                    Log.d("Tracking", "Nueva ubicación: $latLng. Lista completa: ${_routePoints}")
                }
            }
        }

        // Iniciar el contador
        routeTracker.startTimer(viewModelScope)

        //  Observar el elapsedTime de RouteTracker y actualizar el state del ViewModel
        viewModelScope.launch {
            routeTracker.elapsedTime.collect { time ->
                _elapsedTime.value = time
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
        _trackingState.value = true
    }

    // Función para detener las actualizaciones de ubicación
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
        Log.d("Tracking", "Timer detenido. Ruta acabada. Info: \n- Tiempo: ${formatTime(elapsedTime.value)}\n- Distancia: ${getRouteDistance()}\n- Velocidad media: ${getRouteAverageSpeed()}\n-Time: ${getElapsedTime()}")
        _trackingState.value = false

        saveCurrentRoute()

        Log.d("Tracking", "Timer detenido 2. Ruta acabada. Info: \n- Tiempo: ${formatTime(elapsedTime.value)}\n- Distancia: ${getRouteDistance()}\n- Velocidad media: ${getRouteAverageSpeed()}\n-Time: ${getElapsedTime()}")
        routeTracker.stopTimer()

    }

    private fun updateRoutePoints(latLng: LatLng) {
        val currentList = _routePoints.value.toMutableList()

        currentList.add(latLng)
        _routePoints.value = currentList // Asignamos nueva lista para notificar el cambio
    }


    private fun clearRoute() {
        _routePoints.value = emptyList()
    }

    // Obtener la distancia total de la ruta en metros
    fun getRouteDistance(): Double {
        return routeTracker.getDistanceMeters(_routePoints.value)
    }

    // Obtener la velocidad promedio de la ruta en km/h
    fun getRouteAverageSpeed(): Double {
        return routeTracker.getAverageSpeedKmh(_routePoints.value)
    }

    // Obtener el tiempo transcurrido
    fun getElapsedTime(): Long {
        return routeTracker.getElapsedTimeMillis()
    }

    fun generateSampleRoute() {
        val startLat = 40.730610 // Latitud inicial
        val startLng = -73.935242 // Longitud inicial

        val sampleRoute = List(100) { index ->
            // Cambiar latitud y longitud de forma aleatoria
            val randomLat = startLat + Random.nextDouble(-0.001, 0.01) // Variar la latitud aleatoriamente entre -0.001 y 0.001
            val randomLng = startLng + Random.nextDouble(-0.001, 0.01) // Variar la longitud aleatoriamente entre -0.001 y 0.001

            LatLng(randomLat, randomLng)
        }
        _routePoints.value = sampleRoute
    }

    fun simplifyCurrentRoute(
        tolerance: Double = 0.2
    ): List<LatLng> {
        val simplified = RouteSimplifier.simplify(_routePointsSaved.value, tolerance)
        Log.d("Tracking", "Puntos antes: ${_routePointsSaved.value.size}, después: ${simplified.size}")
        return simplified
    }

    fun generateMockRouteFromStartPoint() {
        val start = LatLng(36.4392883, -5.4440983)
        val simulated = generateRealisticSampleRoute(start.latitude, start.longitude, pointCount = 600)
        _routePointsTest.value = simulated
        val adelgazado = RouteSimplifier.simplify(simulated, tolerance = 0.0005	)
        Log.d("Tracking", "Ruta generada: ${_routePointsTest.value.count()} Ruta adelgazada: ${adelgazado.count()}")
    }

}




