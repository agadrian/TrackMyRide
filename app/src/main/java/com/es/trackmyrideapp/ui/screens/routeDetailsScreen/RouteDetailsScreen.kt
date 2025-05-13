package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import FullscreenImageDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.ui.permissions.AppPermission
import com.es.trackmyrideapp.ui.permissions.ClosableBlockedDialog
import com.es.trackmyrideapp.ui.permissions.RationaleDialog
import com.es.trackmyrideapp.ui.permissions.rememberPermissionHandler


@Composable
fun RouteDetailScreen(
    modifier: Modifier = Modifier
){

    val scrollState = rememberScrollState()

    var description by remember { mutableStateOf("test") }
    val startTime by remember { mutableStateOf("15:34") }
    val endTime by remember { mutableStateOf("12:54") }
    val startPoint by remember { mutableStateOf("C/ lamielo 2") }
    val endPoint by remember { mutableStateOf("C/ marquitos 55") }

    val totalDistance by remember { mutableStateOf("C/ mar") }
    val movingTime by remember { mutableStateOf("C/ mar") }
    val avgSpeed by remember { mutableStateOf("C/ mar") }
    val maxSpeed by remember { mutableStateOf("C/ mar") }
    val fuelConsumed by remember { mutableStateOf("C/ mar") }
    val efficiency by remember { mutableStateOf("C/ mar") }

    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.ReadImages
    )

    // Controlar estado de imagenes
    val images = remember { mutableStateListOf<String>() }
    // Es la que se muestra en grande
    val selectedImage = remember { mutableStateOf<String?>(null) }

    // Controlar estado dialogo mapa
    var showMapDialog by remember { mutableStateOf(false) }
    var showPermDialog by remember { mutableStateOf(false) }

    // Lanzador para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            images.add(it.toString())
            // Enviar la imagen al viewmodel, guardarla, etc.
        }
    }

    LaunchedEffect(permissionState.isGranted) {
        if (permissionState.isGranted) {
            // Cuando el permiso es concedido, actualizamos el estado
            showPermDialog = false // Cerrar el diálogo
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
            title = "Route around the mountains",
            date = "1/1/1",
            vehicleType = VehicleType.Car
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
                startTime = startTime,
                endTime = endTime,
                startPoint = startPoint,
                endPoint = endPoint,
                onDescriptionChanged = { description = it }
            )

            // Card Stats
            StatsCard(
                totalDistance = totalDistance,
                movingTime = movingTime,
                avgSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                fuelConsumed = fuelConsumed,
                efficiency = efficiency
            )

            // Card images
            ImagesCard(
                images = images,
                onAddImage = {
                    if (permissionState.isGranted) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        requestPermission()
                    }
                    //images.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQy526UfRatqvWkQaponozTmdlhig4eh6aIXA&s")
                },
                onImageClick = { selectedImage.value = it }
            )

            PremiumCard(
                onUpdateToPremiumClicked = {/*TODO: Navegar a premium screen o al pago directamente*/}
            )

            FooterButtons(
                onExportClicked = {},
                onShareClicked = {}
            )

            Spacer(Modifier.height(8.dp))
        }
    }

    selectedImage.value?.let { imageUrl ->
        FullscreenImageDialog(
            imageUrl = imageUrl,
            onDismiss = { selectedImage.value = null }
        )
    }

    if (showMapDialog) {
        DialogMap { showMapDialog = false }
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






@Composable
@Preview
fun dfs(){
    RouteDetailScreen()
}