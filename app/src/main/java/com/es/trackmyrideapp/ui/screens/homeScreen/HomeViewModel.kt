package com.es.trackmyrideapp.ui.screens.homeScreen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.extensions.round
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.repository.SessionRepository
import com.es.trackmyrideapp.domain.tracker.RouteTracker
import com.es.trackmyrideapp.domain.usecase.routes.CreateRouteUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.GetVehicleByTypeUseCase
import com.es.trackmyrideapp.utils.RouteSimplifier
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val createRouteUseCase: CreateRouteUseCase,
    private val sessionRepository: SessionRepository,
    private val getVehicleByTypeUseCase: GetVehicleByTypeUseCase
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

    private val _uiMessage = mutableStateOf<UiMessage?>(null)
    val uiMessage: State<UiMessage?> = _uiMessage

    fun clearUiMessage() {
        _uiMessage.value = null
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTracking() {
        if (_trackingState.value) {
            stopLocationUpdates()
        } else {
            startLocationUpdates()
        }
    }



    // Iniciar actualizaciones de ubicación
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).apply {
            setMinUpdateDistanceMeters(5f)  // Distancia minima de 5 metros entre actualizaciones
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

    // Detener las actualizaciones de ubicación
    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
        // Parar timer
        routeTracker.stopTimer()
        Log.d("Tracking", "Timer detenido. Ruta acabada. Info: \n- Tiempo: ${formatTime(elapsedTime.value)}\n- Distancia: ${getRouteDistance()}\n- Velocidad media: ${getRouteAverageSpeed()}\n-Time: ${getElapsedTime()}")

        val preciseElapsedTime = routeTracker.getElapsedTimeMillis()
        Log.d("Tracking", "Timer detenido. Tiempo preciso: ${preciseElapsedTime}ms")

        // Guardar ruta
        saveCurrentRoute(){
            clearRoute()
            routeTracker.reset()
        }
        _trackingState.value = false

        Log.d("Tracking", "Timer detenido 2. Ruta acabada. Info: \n- Tiempo: ${formatTime(elapsedTime.value)}\n- Distancia: ${getRouteDistance()}\n- Velocidad media: ${getRouteAverageSpeed()}\n-Time: ${getElapsedTime()}")
    }



    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * Guardar la ruta creando un routeCreateDTO, y usando un callback para limpiar los estados al acabar.
     */
    private fun saveCurrentRoute(onComplete: () -> Unit = {}) {
        // Si no hay puntos, no hacer nada
        val points = _routePoints.value
        if (points.size <= 1) return

        val selectedVehicle = sessionRepository.selectedVehicle.value

        viewModelScope.launch {
            when (val result = getVehicleByTypeUseCase(selectedVehicle)) {
                is Resource.Success -> {
                    val vehicle = result.data
                    Log.d("Tracking", "save ruta antes de stats. ${_elapsedTime.value}")
                    val stats = routeTracker.getCalculatedStats(points, vehicle.efficiency)
                    Log.d("Tracking", "save ruta despues de stats. ${_elapsedTime.value}")

                    Log.d("Tracking", "Stats: $stats")

                    // Crear DTO ruta
                    val routeCreateDTO = RouteCreateDTO(
                        name = "Route day ${LocalDateTime.now()}",
                        description = null,
                        startTime = Instant.ofEpochMilli(routeTracker.startTimeMillis)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        endTime = Instant.ofEpochMilli(System.currentTimeMillis())
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        startPoint = getStreetAndNumber(points.first().latitude, points.first().longitude),
                        endPoint = getStreetAndNumber(points.last().latitude, points.last().longitude),
                        distanceKm = (stats.distanceMeters / 1000.0).round(),
                        movingTimeSec = (stats.elapsedTimeMillis / 1000),
                        avgSpeed = stats.averageSpeedKmh.round(),
                        maxSpeed = stats.maxSpeed.round(),
                        fuelConsumed = stats.fuelConsumed?.round(),
                        efficiency = vehicle.efficiency?.round(),
                        pace = stats.paceSecondsPerKm?.round(),
                        vehicleType = selectedVehicle,
                        compressedPath = simplifyCurrentRoute(points)
                    )
                    val createResult = createRouteUseCase(routeCreateDTO)

                    onComplete()
                    if (createResult is Resource.Success) {
                        _uiMessage.value = UiMessage("Route saved successfully", MessageType.INFO)
                        Log.d("Tracking", "Ruta enviada correctamente.")
                    } else {
                        Log.e("Tracking", "Error al guardar ruta: ${createResult}")
                        if (createResult is Resource.Error){
                            _uiMessage.value = UiMessage("Error saving route", MessageType.ERROR)
                            Log.e("Tracking", "Error al guardar ruta: ${createResult.message}. Code: ${createResult.code}")

                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("Tracking", "Error obteniendo datos del vehículo: ${result.message}")
                }
                Resource.Loading -> TODO()
            }
        }
    }


    private fun getStreetAndNumber(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(appContext, Locale.getDefault())
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
            } else "Not available"
        } catch (e: Exception) {
            Log.e("Tracking", "Error getting street name and number: ${e.message}")
            "Not available"
        }
    }

    /**
     * Comprimir ruta:
     * - 1º RouteSimplifier.simplify(points, 0.00005) -> Simplified
     * - 2º compressRouteWithDelta(simplifiedPoints) -> BinaryData
     * - 3º Base64.encodeToString(binaryData, Base64.NO_WRAP) -> Base 64
     */
//    fun loadGpxRoute() {
//        viewModelScope.launch {
//            val points = parseWikilocGpx(appContext, R.raw.miruta)
//            val startPoint = points.firstOrNull()
//            val endPoint = points.lastOrNull()
//
//            val startStreet = startPoint?.let { getStreetAndNumber(it.latitude, it.longitude) }
//            val endStreet = endPoint?.let { getStreetAndNumber(it.latitude, it.longitude) }
//
//            val base64ForApi =RouteSimplifier.compressRoute(points, 0.00005)
//
//            Log.d("Tracking", "Puntos cargados: ${points.size}. Puntos simplificados: ${simpli.size}. Puntos binarydata:  ${binaryData.size}. Base64: ${base64ForApi.length}. Start street: $startStreet. End street: $endStreet")
//            Log.d("Tracking", "Base64: $base64ForApi")
//
////            val binaryDecompressed = Base64.decode(base64ForApi, Base64.NO_WRAP)
////            val pointsDecompressed = decompressRoute(binaryDecompressed)
////            Log.d("Tracking", "Puntos descomprimidos: ${pointsDecompressed.size}")
//
//        }
//    }



    private fun updateRoutePoints(latLng: LatLng) {
        val currentPointsList = _routePoints.value.toMutableList()
        routeTracker.updateLocation(latLng)
        currentPointsList.add(latLng)
        _routePoints.value = currentPointsList // Asignamos nueva lista para notificar el cambio
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

    private fun simplifyCurrentRoute(points: List<LatLng>, tolerance: Double = 0.00005): String{
        return RouteSimplifier.compressRoute(points, tolerance)
    }

//    private fun simplifyCurrentRoute(
//        points: List<LatLng>
//    ): String {
//        val simpli = RouteSimplifier.simplify(points, 0.00005)
//        val binaryData = compressRouteWithDelta(simpli)
//        val base64ForApi = Base64.encodeToString(binaryData, Base64.NO_WRAP)
//
//        return base64ForApi
//    }

//    fun generateMockRouteFromStartPoint() {
//        val start = LatLng(36.4392883, -5.4440983)
//        val simulated = generateRealisticSampleRoute(start.latitude, start.longitude, pointCount = 600)
//        _routePointsTest.value = simulated
//        val adelgazado = RouteSimplifier.simplify(simulated, tolerance = 0.0005	)
//        Log.d("Tracking", "Ruta generada: ${_routePointsTest.value.count()} Ruta adelgazada: ${adelgazado.count()}")
//    }

}




