package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import FullscreenImageDialog
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.domain.model.RouteImage
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.ui.permissions.AppPermission
import com.es.trackmyrideapp.ui.permissions.ClosableBlockedDialog
import com.es.trackmyrideapp.ui.permissions.RationaleDialog
import com.es.trackmyrideapp.ui.permissions.rememberPermissionHandler


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RouteDetailScreen(
    modifier: Modifier = Modifier,
    onGoPremiumClicked: () -> Unit,
    idRoute: Long,
    snackbarHostState: SnackbarHostState
){

    val routeDetailViewModel: RouteDetailViewModel = hiltViewModel()
    val uiMessage by routeDetailViewModel.uiMessage.collectAsState()
    val context = LocalContext.current

    //  Llamo con launchedeffect a la api para comprobar el premium, y luego miro el estado obtenido
    val sessionViewModel = LocalSessionViewModel.current
    val isPremium by sessionViewModel.isPremium.collectAsState()


    LaunchedEffect(Unit){
        sessionViewModel.checkPremiumStatus()
    }

    val scrollState = rememberScrollState()

    val title by routeDetailViewModel.name
    val date by routeDetailViewModel.startDateTime
    val description by routeDetailViewModel.description
    val startTime by routeDetailViewModel.startTime
    val endTime by routeDetailViewModel.endTime
    val startPoint by routeDetailViewModel.startPoint
    val endPoint by routeDetailViewModel.endPoint

    val totalDistance by routeDetailViewModel.distanceKm
    val movingTime by routeDetailViewModel.movingTimeSec
    val avgSpeed by routeDetailViewModel.avgSpeed
    val maxSpeed by routeDetailViewModel.maxSpeed
    val fuelConsumed by routeDetailViewModel.fuelConsumed
    val efficiency by routeDetailViewModel.efficiency
    val vehicleType by routeDetailViewModel.vehicleType

    val routePoints by routeDetailViewModel.routePoints
    val startPointName by routeDetailViewModel.startPoint
    val endPointName by routeDetailViewModel.endPoint

    val images = routeDetailViewModel.uploadedImages

    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.ReadImages
    )

    // Imagen del dialog grande
    val selectedImage = remember { mutableStateOf<RouteImage?>(null) }

    // Controlar estado dialogo mapa
    var showMapDialog by remember { mutableStateOf(false) }
    var showPermDialog by remember { mutableStateOf(false) }

    var shouldLaunchPicker by remember { mutableStateOf(false) }

    // Lanzador para seleccionar imagen y enviarla al viewmodel
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            routeDetailViewModel.uploadImage(it)
        }
    }

    // Mostrar info  en snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message.message,
                withDismissAction = message.type == MessageType.ERROR,
                duration = SnackbarDuration.Short
            )
            routeDetailViewModel.consumeUiMessage()
        }
    }

    // Control flujo permiso + picker
    LaunchedEffect(shouldLaunchPicker, permissionState.isGranted) {
        if (shouldLaunchPicker) {
            if (permissionState.isGranted) {
                imagePickerLauncher.launch("image/*")
                shouldLaunchPicker = false
            } else {
                requestPermission()
            }
        }
    }


    LaunchedEffect(permissionState.isGranted) {
        if (permissionState.isGranted) {
            // Cuando el permiso es concedido, actualizamos el estado
            showPermDialog = false
        }
    }


    if (showPermDialog) {
        ClosableBlockedDialog(
            onDismiss = { showPermDialog = false },
            onResumeCheck = {
                // Cuando regresamos de ajustes, verificar el permiso nuevamente
                requestPermission()
            }
        )
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
        ,
    ){

        // Imagen mapa con boton Full Map
        RouteImagePreview(
            onFullMapClicked = { showMapDialog = true }
        )

        // Titulo, fecha, tipo veh
        TittleInfo(
            modifier = Modifier
                .background(colorResource(R.color.lightGray))
                .padding(horizontal = 30.dp),
            title = title,
            date = date,
            vehicleType = vehicleType ?: VehicleType.CAR
        )

        Spacer(Modifier.height(24.dp))

        if (isPremium){
            Log.d("Flujotest", "Es premium")
        }else{
            Log.d("Flujotest", "No es premium")
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {


            // Card general Info
            GeneralInfoCard(
                description = description,
                startTime = "$startTime h",
                endTime = "$endTime h",
                startPoint = startPoint,
                endPoint = endPoint,
                onDescriptionChanged = { routeDetailViewModel.onDescriptionChanged(it) },
            )

            // Card Stats
            StatsCard(
                totalDistance = "$totalDistance Km",
                movingTime = "$movingTime h",
                avgSpeed = "$avgSpeed Km/h",
                maxSpeed = "$maxSpeed Km/h",
                fuelConsumed = "$fuelConsumed L",
                efficiency = "$efficiency Km/L"
            )

            // Card images
            ImagesCard(
                images = images,
                onAddImage = {
                    val canAdd = routeDetailViewModel.canAddMoreImages(isPremium, images.size)
                    shouldLaunchPicker = canAdd
                },
                onImageClick = { selectedImage.value = it }
            )

            if (!isPremium){
                PremiumCard(
                    onUpdateToPremiumClicked = onGoPremiumClicked
                )
            }

            if (isPremium){
                FooterButtons(
                    onExportClicked = {
                        routeDetailViewModel.exportRouteToDownloads(context = context)
                    },
                    onShareClicked = {
                        routeDetailViewModel.shareRouteAsGpx(context = context)
                    },
                    /*
                    onImportClicked = { uri ->
                        routeDetailViewModel.importGpxFromUri(uri, context)
                    }

                     */
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    selectedImage.value?.let { image ->
        FullscreenImageDialog(
            image = image,
            onDismiss = { selectedImage.value = null },
            onDelete = { imageId ->
                routeDetailViewModel.deleteImage(imageId)
                selectedImage.value = null

            }
        )
    }

    if (showMapDialog) {
        DialogMap(
            onDismissRequest = { showMapDialog = false },
            routePoints = routePoints,
            startPointName = startPointName,
            endPointName = endPointName
        )
    }

    when {
        permissionState.shouldShowRationaleDialog -> {
            RationaleDialog(
                onRetry = requestPermission,
                msg = "Gallery access is needed to attach images to this route."
            )
        }

        permissionState.shouldShowSystemDialog -> {
            LaunchedEffect(Unit) {
                requestPermission()
            }
        }
    }

    // Controlar apertura del diálogo de bloqueo
    LaunchedEffect(permissionState.shouldShowBlockedDialog) {
        showPermDialog = permissionState.shouldShowBlockedDialog
    }
}




