package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.RouteDetailsConstants
import com.es.trackmyrideapp.core.extensions.round
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.data.remote.dto.RouteImageRequest
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.model.RouteImage
import com.es.trackmyrideapp.domain.usecase.images.DeleteRouteImageUseCase
import com.es.trackmyrideapp.domain.usecase.images.GetRouteImagesUseCase
import com.es.trackmyrideapp.domain.usecase.images.UploadImageToCloudinaryUseCase
import com.es.trackmyrideapp.domain.usecase.images.UploadRouteImagesUseCase
import com.es.trackmyrideapp.domain.usecase.routes.GetRouteByIdUseCase
import com.es.trackmyrideapp.domain.usecase.routes.UpdateRouteUseCase
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.utils.GPXParser
import com.es.trackmyrideapp.utils.GPXParser.saveGpxToDownloads
import com.es.trackmyrideapp.utils.RouteSimplifier
import com.es.trackmyrideapp.utils.TimeFormatter
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val uploadImageToCloudinaryUseCase: UploadImageToCloudinaryUseCase,
    private val uploadRouteImageUseCase: UploadRouteImagesUseCase,
    private val getRouteImagesUseCase: GetRouteImagesUseCase,
    private val deleteRouteImageUseCase: DeleteRouteImageUseCase,
    private val updateRouteUseCase: UpdateRouteUseCase
) : ViewModel() {

    private val routeId: Long = checkNotNull(savedStateHandle["routeId"])

    private var loadedRoute: Route? = null

    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun consumeUiMessage() {
        _uiMessage.value = null
    }

    var routePoints = mutableStateOf<List<LatLng>>(emptyList())
        private set

    var title = mutableStateOf("")
        private set

    var description = mutableStateOf("")
        private set

    var nameError  = mutableStateOf<String?>(null)
        private set

    var descriptionError = mutableStateOf<String?>(null)
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

    var uploadedImages = mutableStateListOf<RouteImage>()
        private set

    fun onTitleChanged(newName: String) {
        title.value = newName
        nameError.value = validateName(newName)
    }

    fun onDescriptionChanged(newDescription: String) {
        description.value = newDescription
        descriptionError.value = validateDescription(newDescription)
    }

    private fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "Title cannot be empty"
            value.length > RouteDetailsConstants.MAX_NAME_LENGTH -> "Max ${RouteDetailsConstants.MAX_NAME_LENGTH} characters"
            else -> null
        }
    }

    private fun validateDescription(value: String): String? {
        return if (value.length > RouteDetailsConstants.MAX_DESC_LENGTH) "Max ${RouteDetailsConstants.MAX_DESC_LENGTH} characters" else null
    }

    fun validateAll(): Boolean {
        nameError.value = validateName(title.value)
        descriptionError.value = validateDescription(description.value)

        return nameError.value == null && descriptionError.value == null
    }


    init {
        fetchRouteAndPopulateStates()
        fetchRouteImages()
    }


    fun uploadImage(uri: Uri) {
        Log.d("flujotest", "uploadimage llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val imageUrl = uploadImageToCloudinaryUseCase(uri)
            if (imageUrl != null) {
                val request = RouteImageRequest(
                    imageUrl = imageUrl,
                    description = null
                )
                uploadRouteImageUseCase(routeId, request)
                fetchRouteImages()
                _uiState.value = UiState.Idle
            } else {
                _uiMessage.value = UiMessage("Image upload failed", MessageType.ERROR)
                _uiState.value = UiState.Idle
            }
        }
    }

    fun canAddMoreImages(isPremium: Boolean, currentImageCount: Int): Boolean {
        val maxImages = if (isPremium) RouteDetailsConstants.MAX_IMAGES_PREMIUM else RouteDetailsConstants.MAX_IMAGES_NO_PREMIUM
        return if (currentImageCount < maxImages) {
            true
        } else {
            _uiMessage.value = UiMessage("You have reached the maximum number of images ($maxImages) ${if (!isPremium) "Get premium to add more" else ""}", MessageType.INFO)
            false
        }
    }


    private fun fetchRouteImages() {
        Log.d("flujotest", "fetchrouteimages llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = getRouteImagesUseCase(routeId)) {
                is Resource.Success -> {
                    result.data.let { images ->
                        uploadedImages.clear() // Evitar duplicados
                        uploadedImages.addAll(images)
                    }
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Error loading images", MessageType.ERROR)
                }
            }
            _uiState.value = UiState.Idle
        }
    }


    private fun deleteImage(imageId: Long) {
        Log.d("flujotest", "deleteimage llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = deleteRouteImageUseCase(routeId, imageId)) {
                is Resource.Success -> {
                    fetchRouteImages()
                    _uiState.value = UiState.Idle
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Failed to delete image.", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }


    private fun fetchRouteAndPopulateStates() {
        Log.d("flujotest", "FetchRutesandpopulatesttates llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = getRouteByIdUseCase(routeId)) {
                is Resource.Success -> {
                    result.data?.let { route ->
                        loadedRoute = route

                        // Asignar a estados individuales
                        title.value = route.name
                        description.value = route.description.orEmpty()
                        startTime.value = route.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        endTime.value = route.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        startPoint.value = route.startPoint
                        startDateTime.value =route.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                        endPoint.value = route.endPoint
                        distanceKm.value = route.distanceKm.round().toString()
                        movingTimeSec.value = TimeFormatter.formatSecondsToHhMmSs(route.movingTimeSec)
                        avgSpeed.value = route.avgSpeed.round().toString()
                        maxSpeed.value = route.maxSpeed.round().toString()
                        fuelConsumed.value = route.fuelConsumed?.round()?.toString() ?: "-"
                        efficiency.value = route.efficiency?.round()?.toString() ?: "-"
                        pace.value = route.pace?.round()?.toString() ?: "-"
                        vehicleType.value = route.vehicleType
                    }

                    routePoints.value = getDecodedRoutePoints()
                    _uiState.value = UiState.Idle
                }


                is Resource.Error -> {
                    Log.e("RouteDetailVM", "Error loading route: ${result.message}")
                    _uiState.value = UiState.Idle
                    // TODO("Usar ApiErrorHandler")
                }
            }
        }
    }

    private fun getDecodedRoutePoints(): List<LatLng> {
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

    fun updateRoute() {
        Log.d("flujotest", "updateroute llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val routeUpdateDTO = RouteUpdateDTO(
                name = title.value,
                description = description.value,
            )

            when (val result = updateRouteUseCase(routeId, routeUpdateDTO)) {
                is Resource.Success -> {
                    _uiMessage.value = UiMessage("Route updated successfully", MessageType.INFO)
                    _uiState.value = UiState.Idle
                    fetchRouteAndPopulateStates() // Recargar datos actualizados
                }

                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Error updating route: ${result.message}", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                    // TODO("Usar ApiErrorHandler")
                }
            }
        }
    }


    fun shareRouteAsGpx(context: Context) {
        val points = routePoints.value
        if (points.isEmpty()) {
            _uiMessage.value = UiMessage("No route points to export", MessageType.ERROR)
            return
        }

        val gpx = GPXParser.generateGpx(points)
        GPXParser.shareRouteAsGpx(context, gpx)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportRouteToDownloads(context: Context) {
        val gpx = GPXParser.generateGpx(routePoints.value)
        val fileName = "route_${System.currentTimeMillis()}.gpx"
        val success = saveGpxToDownloads(context, fileName, gpx)

        if (success) {
            _uiMessage.value = UiMessage("Route saved to Downloads", MessageType.INFO)
        } else {
            _uiMessage.value = UiMessage("An error occurred while saving the route", MessageType.ERROR)
        }
    }

    /*
    fun importGpxFromUri(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val gpxContent = inputStream.bufferedReader().use { it.readText() }
                    // Aquí parseamos el GPX con tu parser
                    val points = GPXParser.parseGpx(gpxContent)

                    if (points.isNotEmpty()) {
                        //routePoints.value = points
                        _uiMessage.value = UiMessage("GPX imported successfully", MessageType.INFO)
                    } else {
                        _uiMessage.value = UiMessage("The GPX file contains no points", MessageType.ERROR)
                    }
                } ?: run {
                    _uiMessage.value = UiMessage("Could not open the selected file", MessageType.ERROR)
                }
            } catch (e: Exception) {
                _uiMessage.value = UiMessage("Error importing GPX: ${e.message}", MessageType.ERROR)
            }
        }
    }
    */

    /* Gestion estados imagenes */

    var selectedImage = mutableStateOf<RouteImage?>(null)
        private set

    fun selectImage(image: RouteImage) {
        selectedImage.value = image
    }

    fun clearSelectedImage() {
        selectedImage.value = null
    }


    /*  Dialog Imagen  */
    private val _imagePendingDeletion = MutableStateFlow<RouteImage?>(null)
    val imagePendingDeletion: StateFlow<RouteImage?> = _imagePendingDeletion

    fun requestImageDeletion(image: RouteImage) {
        _imagePendingDeletion.value = image
    }

    fun confirmImageDeletion() {
        _imagePendingDeletion.value?.let { image ->
            deleteImage(image.id)

            if (selectedImage.value?.id == image.id) {
                clearSelectedImage()
            }
        }
        _imagePendingDeletion.value = null
    }

    fun cancelImageDeletion() {
        _imagePendingDeletion.value = null
    }


    /* Dialog ShowMap */
    private val _showMapDialog = MutableStateFlow(false)
    val showMapDialog: StateFlow<Boolean> = _showMapDialog

    fun openMapDialog() {
        _showMapDialog.value = true
    }

    fun closeMapDialog() {
        _showMapDialog.value = false
    }


    fun appendUnit(value: String, unit: String): String {
        return if (value == "-") "-" else "$value $unit"
    }


}