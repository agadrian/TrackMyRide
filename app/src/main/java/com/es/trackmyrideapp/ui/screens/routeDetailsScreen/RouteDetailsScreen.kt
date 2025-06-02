package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import FullscreenImageDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.ui.components.ConfirmationDialog
import com.es.trackmyrideapp.ui.components.CustomButton
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
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val uiState by routeDetailViewModel.uiState.collectAsState()

    //  Llamo con launchedeffect a la api para comprobar el premium, y luego miro el estado obtenido
    val sessionViewModel = LocalSessionViewModel.current
    val isPremium by sessionViewModel.isPremium.collectAsState()
    val isEditing by sessionViewModel.isEditingRouteDetails.collectAsState()

    LaunchedEffect(Unit){
        sessionViewModel.checkPremiumStatus()
    }


    val title by routeDetailViewModel.title
    val date by routeDetailViewModel.startDateTime
    val description by routeDetailViewModel.description
    val titleError by routeDetailViewModel.nameError
    val descriptionError by routeDetailViewModel.descriptionError

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


    // Imagen del dialog grande
    val selectedImage by routeDetailViewModel.selectedImage
    val imagePendingDeletion by routeDetailViewModel.imagePendingDeletion.collectAsState()

    // Dialog del mapa
    val showMapDialog by routeDetailViewModel.showMapDialog.collectAsState()

    // Permisos
    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.ReadImages
    )

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

    // CircularProgessIndicator
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Loading -> sessionViewModel.showLoading()
            else -> sessionViewModel.hideLoading()
        }
    }

    // Snackbar msg
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            sessionViewModel.showSnackbar(
                UiSnackbar(
                    message = message.message,
                    messageType = message.type,
                    withDismissAction = true
                )
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

    // Resetear la edicion por si cambiamos de pantalla sin quitarla
    DisposableEffect(Unit) {
        onDispose {
            sessionViewModel.setEditingRouteDetails(false)
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
            .clickable(
                // Evita que el click consuma otros eventos
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
    ){

        // Imagen mapa con boton Full Map
        RouteImagePreview(
            onFullMapClicked = { routeDetailViewModel.openMapDialog() }
        )

        // Titulo, fecha, tipo veh
        TittleInfo(
            modifier = Modifier
                .background(colorResource(R.color.lightGray))
                .padding(horizontal = 30.dp),
            title = title,
            date = date,
            vehicleType = vehicleType ?: VehicleType.CAR,
            isEditing = isEditing,
            onTitleChange = { routeDetailViewModel.onTitleChanged(it) },
            titleError = titleError
        )

        Spacer(Modifier.height(24.dp))


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
                isEditable = isEditing,
                descriptionError = descriptionError
            )

            // Boton Save cuando esta en modo edicion
            if (isEditing){
                CustomButton(
                    onclick = {
                        routeDetailViewModel.updateRoute()
                        sessionViewModel.setEditingRouteDetails(false) },
                    text = "Save changes",
                    buttonColor = MaterialTheme.colorScheme.primary,
                    fontColor = colorResource(R.color.black),
                    enabled = routeDetailViewModel.validateAll()
                )
            }

            // Card Stats
            StatsCard(
                totalDistance = "$totalDistance Km",
                movingTime = movingTime,
                avgSpeed = "$avgSpeed Km/h",
                maxSpeed = "$maxSpeed Km/h",
                fuelConsumed = routeDetailViewModel.appendUnit(fuelConsumed, "L"),
                efficiency = routeDetailViewModel.appendUnit(efficiency, "Km/L")
            )

            // Card images
            ImagesCard(
                images = images,
                onAddImage = {
                    val canAdd = routeDetailViewModel.canAddMoreImages(isPremium, images.size)
                    shouldLaunchPicker = canAdd
                },
                onImageClick = {
                    routeDetailViewModel.selectImage(it)
                }
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

    selectedImage?.let { image ->
        FullscreenImageDialog(
            image = image,
            onDismiss = { routeDetailViewModel.clearSelectedImage() },
            onDelete = {
                routeDetailViewModel.requestImageDeletion(image)
            }
        )
    }

    imagePendingDeletion?.let {
        ConfirmationDialog(
            title = "Delete Image",
            message = "Are you sure you want to delete this image?",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            onConfirm = {
                routeDetailViewModel.confirmImageDeletion()
            },
            onDismiss = {
                routeDetailViewModel.cancelImageDeletion()
            }
        )
    }


    if (showMapDialog) {
        DialogMap(
            onDismissRequest = { routeDetailViewModel.closeMapDialog() },
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




