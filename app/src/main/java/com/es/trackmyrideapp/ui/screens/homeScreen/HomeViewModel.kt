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
import com.es.trackmyrideapp.HomeScreenConstants
import com.es.trackmyrideapp.HomeScreenConstants.DEFAULT_BEARING
import com.es.trackmyrideapp.HomeScreenConstants.DEFAULT_TILT
import com.es.trackmyrideapp.HomeScreenConstants.INTERVAL_MILIS_LOCATION_UPDATES
import com.es.trackmyrideapp.HomeScreenConstants.MINIMUN_DISTANCE_METERS
import com.es.trackmyrideapp.HomeScreenConstants.TRACKING_TILT
import com.es.trackmyrideapp.core.extensions.round
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.core.states.UiState
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
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.random.Random


/**
 * FLUJO:
 * - Se intenta obtener la ubicacion conocida en el init, si no puede, solicita nuevas para obtener la inicial
 * - Al pulsar boton de innicar ruta, e activa toogletracking, que actualiza los estados, y llama a la funcion de startLocationUpdates
 * - Esta funcione mpieza a recibir ubicaciones. añadiendose a la lista de routepoints y actualizando la direcciond e la camara, dibujandose la polilyne etc.
 * - Al detener, se compruevba que hayan suficientes puntos o no para guardar la ruta, si los hay, se detiene la escucha de ubicaciones, se calcula los datos (tiempo, estadisticas, etc) y se guarda en la bd para posteriormente recuperarla en otr apantalla y poder visualzarla.
 * - Ademas, se centra la camara en el puntoa ctual, se limpian los estados para si se quiere grabar otra ruta, se resetea timer, y todo.
 *
 */


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


    private val _currentLocation = mutableStateOf<LatLng?>(null)
    val currentLocation: State<LatLng?> = _currentLocation

    private var locationCallback: LocationCallback? = null

    // Puntos gps acortados
    private val _simplifiedRoutePoints = mutableStateOf<List<LatLng>>(emptyList())
    val simplifiedRoutePoints: State<List<LatLng>> = _simplifiedRoutePoints


    // Velocidad
    val currentSpeedKmhFlow: StateFlow<Double> = routeTracker.currentSpeedFlow

    private val _bearing = mutableStateOf(DEFAULT_BEARING)
    val bearing: State<Float> = _bearing

    private val _lastStopLocation = mutableStateOf<LatLng?>(null)
    val lastStopLocation: State<LatLng?> = _lastStopLocation

    private val _cameraTilt = mutableStateOf(0f)
    val cameraTilt: State<Float> = _cameraTilt

    private val _shouldResetCamera = mutableStateOf(false)
    val shouldResetCamera: State<Boolean> = _shouldResetCamera


    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Ui Messages
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage


    fun consumeUiMessage() {
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

    /**
     * Obtiene la última ubicación conocida del dispositivo.
     * Si falla, solicita una actualización de ubicación manualmente.
     */
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

    /**
     * Solicita una ubicación actualizada solo una vez si no se tiene una última ubicación conocida.
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdatesForInitialPosition() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILIS_LOCATION_UPDATES).apply {
            setIntervalMillis(INTERVAL_MILIS_LOCATION_UPDATES)  // El intervalo más rápido
            setMaxUpdates(2) // 2 POR SI FALLARA LA PRIMERA
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


    /**
     * Establece el estado de tracking y ajusta la cámara del mapa.
     */
    private fun setTrackingState(isTracking: Boolean) {
        _trackingState.value = isTracking
        updateCameraOrientation(isTracking)
        _shouldResetCamera.value = !isTracking
    }

    /**
     * Actualiza la orientación de la cámara dependiendo del modo de tracking.
     */
    private fun updateCameraOrientation(isTracking: Boolean) {
        _cameraTilt.value = if (isTracking) TRACKING_TILT else DEFAULT_TILT
        _bearing.value = if (isTracking) _bearing.value else DEFAULT_BEARING
    }


    /**
     * Activa o desactiva el modo de seguimiento GPS.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTracking() {
        val newState = !_trackingState.value
        setTrackingState(newState)

        if (newState) {
            startLocationUpdates()
        } else {
            viewModelScope.launch {
                stopLocationUpdates()
                _bearing.value = 0f
            }
        }
    }


    /**
     * Solicita la ubicación actual del usuario una sola vez de forma suspendida.
     * @return LatLng o último punto de ruta si no se puede obtener
     */
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationOnce(): LatLng? {
        return try {
            val location = withTimeoutOrNull(INTERVAL_MILIS_LOCATION_UPDATES) {
                suspendCancellableCoroutine { cont ->
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { location ->
                        cont.resume(location?.let { LatLng(it.latitude, it.longitude) })
                    }.addOnFailureListener {
                        cont.resume(null)
                    }
                }
            }
            location ?: _routePoints.value.lastOrNull()
        } catch (e: Exception) {
            _routePoints.value.lastOrNull()
        }
    }

    /**
     * Inicia el seguimiento GPS, actualizaciones de ubicación y el cronómetro.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        viewModelScope.launch {
            // Obtener ubicación actual real
            val initialLocation = getCurrentLocationOnce()

            // Agregar primer punto si se obtuvo bien
            initialLocation?.let {
                val currentPointsList = _routePoints.value.toMutableList()
                currentPointsList.add(it)
                _routePoints.value = currentPointsList
                Log.d("Tracking", "Primer punto agregado: $it")
            }

            // Configurar solicitud de ubicación
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                INTERVAL_MILIS_LOCATION_UPDATES
            ).apply {
                setMinUpdateDistanceMeters(MINIMUN_DISTANCE_METERS)
                setWaitForAccurateLocation(true)
            }.build()

            // Definir callback
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

            // Observar tiempo transcurrido
            launch {
                routeTracker.elapsedTime.collect { time ->
                    _elapsedTime.value = time
                }
            }

            // Iniciar actualizaciones
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            _trackingState.value = true
        }
    }


    /**
     * Detiene el seguimiento GPS, guarda la ruta y resetea estados.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationProviderClient.removeLocationUpdates(callback)
            locationCallback = null
        }

        // Obtener ubicacion actual y guardarla para centrar el mapa en esta ubicacion al acabar
        val lastLocation = getCurrentLocationOnce()
        _lastStopLocation.value = lastLocation


        // Parar timer
        val preciseElapsedTime = routeTracker.getElapsedTimeMillis()
        Log.d("Tracking", "Tiempo preciso antes de detener timer: ${preciseElapsedTime}ms")

        routeTracker.stopTimer()
        Log.d("Tracking", "Timer detenido. Tiempo despues de stoptimer: ${routeTracker.getElapsedTimeMillis()}ms")


        // Guardar ruta
        saveCurrentRoute(
            duration = preciseElapsedTime,
            onComplete = {
                clearRoute()
                routeTracker.reset()
            }
        )
        _trackingState.value = false
    }



    /**
     * Guarda la ruta actual usando los datos recolectados y la API de backend.
     * @param duration tiempo total de la ruta en milisegundos
     * @param onComplete callback llamado al finalizar
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveCurrentRoute(onComplete: () -> Unit = {}, duration: Long) {
        val points = _routePoints.value

        // No guardar si es corta por puntos o tiempo
        if (points.size < HomeScreenConstants.MIN_ROUTE_POINTS || duration < HomeScreenConstants.MIN_DURATION_MILLIS) {
            _uiMessage.value = UiMessage("Route too short to save", MessageType.ERROR)
            onComplete()
            return
        }

        val selectedVehicle = sessionRepository.selectedVehicle.value

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = getVehicleByTypeUseCase(selectedVehicle)) {
                is Resource.Success -> {
                    val vehicle = result.data
                    Log.d("Tracking", "save ruta antes de stats. ${_elapsedTime.value}")

                    // Obtener estadisticas calculadas
                    val stats = routeTracker.getCalculatedStats(points, vehicle.efficiency)
                    Log.d("Tracking", "save ruta despues de stats. ${_elapsedTime.value}")

                    Log.d("Tracking", "Stats: $stats")

                    // Crear DTO ruta
                    val routeCreateDTO = RouteCreateDTO(
                        name = "Route day ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))}",
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

                    onComplete()

                    when (val createResult = createRouteUseCase(routeCreateDTO)){
                        is Resource.Success -> {
                            _uiState.value = UiState.Idle
                            _uiMessage.value = UiMessage("Route saved successfully", MessageType.INFO)
                            Log.d("Tracking", "Ruta enviada correctamente.")
                        }
                        is Resource.Error -> {
                            _uiState.value = UiState.Idle
                            _uiMessage.value = UiMessage("Error saving route", MessageType.ERROR)
                            Log.e("Tracking", "Error al guardar ruta: ${createResult.message}. Code: ${createResult.code}")
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("Tracking", "Error obteniendo datos del vehículo: ${result.message}")
                    _uiState.value = UiState.Idle
                    _uiMessage.value = UiMessage("Error getting vehicle data, please try again later", MessageType.ERROR)
                }
            }
        }
    }

    /**
     * Devuelve una dirección en texto a partir de coordenadas GPS.
     */
    private fun getStreetAndNumber(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(appContext, Locale.getDefault())
            if (!Geocoder.isPresent()) return "Geocoder not available"

            val addresses = geocoder.getFromLocation(lat, lon, 1) ?: return "No address found"

            addresses.firstOrNull()?.let { address ->
                buildString {
                    append(address.thoroughfare ?: "")
                    if (!address.subThoroughfare.isNullOrEmpty()) {
                        if (isNotEmpty()) append(", ")
                        append(address.subThoroughfare)
                    }
                    if (isEmpty()) append(address.getAddressLine(0) ?: "Unnamed location")
                }
            } ?: "No address data"
        } catch (e: IOException) {
            Log.e("Tracking", "Network error in geocoding: ${e.message}")
            "Network error"
        } catch (e: IllegalArgumentException) {
            Log.e("Tracking", "Invalid coordinates: ${e.message}")
            "Invalid location"
        } catch (e: Exception) {
            Log.e("Tracking", "Unexpected geocoding error: ${e.javaClass.simpleName}")
            "Unknown location"
        }
    }


    /**
     * Añade un nuevo punto a la ruta y calcula el ángulo de dirección par ausar en al camara (bearing).
     */
    private fun updateRoutePoints(latLng: LatLng) {
        val currentPointsList = _routePoints.value.toMutableList()
        routeTracker.updateLocation(latLng)
        currentPointsList.add(latLng)
        _routePoints.value = currentPointsList // Asignamos nueva lista para notificar el cambio

        // Calcular bearing si hay al menos dos puntos
        if (currentPointsList.size >= 2) {
            val lastIndex = currentPointsList.lastIndex
            val from = currentPointsList[lastIndex - 1]
            val to = currentPointsList[lastIndex]
            val bearingDegrees = SphericalUtil.computeHeading(from, to)
            val adjustedBearing = ((bearingDegrees + 360) % 360).toFloat()
            _bearing.value = adjustedBearing
        }
    }

    /** Borra todos los puntos de la ruta actual */
    private fun clearRoute() {
        _routePoints.value = emptyList()
    }

    /** Retorna la distancia recorrida en metros */
    fun getRouteDistance(): Double {
        return routeTracker.getDistanceMeters(_routePoints.value)
    }

    /** Retorna la velocidad promedio en km/h */
    fun getRouteAverageSpeed(): Double {
        return routeTracker.getAverageSpeedKmh(_routePoints.value)
    }

    /** Retorna el tiempo transcurrido en milisegundos */
    fun getElapsedTime(): Long {
        return routeTracker.getElapsedTimeMillis()
    }


    /**
     * Comprime los puntos de una ruta usando un simplificador de rutas.
     * @param points Lista de LatLng a comprimir
     * @param tolerance Tolerancia del algoritmo de simplificación
     * @return Ruta codificada en String
     */
    private fun simplifyCurrentRoute(points: List<LatLng>, tolerance: Double = 0.00005): String{
        return RouteSimplifier.compressRoute(points, tolerance)
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
}




