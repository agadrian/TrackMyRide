package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.extensions.round
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.usecase.routes.GetRouteByIdUseCase
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.utils.RouteSimplifier
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
) : ViewModel() {

    private val routeId: Long = checkNotNull(savedStateHandle["routeId"])

    private var loadedRoute: Route? = null

    var routePoints = mutableStateOf<List<LatLng>>(emptyList())
        private set

    var name = mutableStateOf("")
        private set

    var description = mutableStateOf("")
        private set

    var startTime = mutableStateOf("")
        private set

    var startDateTime = mutableStateOf("")
        private set

    var endTime = mutableStateOf("")
        private set

    var startPoint = mutableStateOf("")
        private set

    var endPoint = mutableStateOf("")
        private set

    var distanceKm = mutableStateOf("")
        private set

    var movingTimeSec = mutableStateOf("")
        private set

    var avgSpeed = mutableStateOf("")
        private set

    var maxSpeed = mutableStateOf("")
        private set

    var fuelConsumed = mutableStateOf("")
        private set

    var efficiency = mutableStateOf("")
        private set

    var pace = mutableStateOf("")
        private set

    var vehicleType = mutableStateOf<VehicleType?>(null)
        private set


    fun onNameChanged(newName: String) {
        name.value = newName
    }

    fun onDescriptionChanged(newDescription: String) {
        description.value = newDescription
    }



    init {
        fetchRouteAndPopulateStates()
    }


    private fun fetchRouteAndPopulateStates() {
        viewModelScope.launch {
            when (val result = getRouteByIdUseCase(routeId)) {
                is Resource.Success -> {
                    result.data?.let { route ->
                        loadedRoute = route

                        // Asignar a estados individuales
                        name.value = route.name
                        description.value = route.description.orEmpty()
                        startTime.value = route.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        endTime.value = route.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        startPoint.value = route.startPoint
                        startDateTime.value =route.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                        endPoint.value = route.endPoint
                        distanceKm.value = route.distanceKm.round().toString()
                        movingTimeSec.value = formatSecondsToHhMmSs(route.movingTimeSec)
                        avgSpeed.value = route.avgSpeed.round().toString()
                        maxSpeed.value = route.maxSpeed.round().toString()
                        fuelConsumed.value = route.fuelConsumed?.round().toString() ?: "-"
                        efficiency.value = route.efficiency?.round().toString() ?: "-"
                        pace.value = route.pace?.round().toString() ?: "-"
                        vehicleType.value = route.vehicleType
                    }

                    routePoints.value = getDecodedRoutePoints()
                }


                is Resource.Error -> {
                    Log.e("RouteDetailVM", "Error loading route: ${result.message}")
                }

                else -> Unit
            }
        }
    }

    fun getDecodedRoutePoints(): List<LatLng> {
        val encoded = loadedRoute?.compressedRoute
        return if (!encoded.isNullOrEmpty()) {
            try {
                RouteSimplifier.decompressRoute(encoded)
            } catch (e: Exception) {
                // Puedes loggear o mostrar error si algo va mal
                Log.e("FlujoTest", "Error decoding route: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun formatSecondsToHhMmSs(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }




}