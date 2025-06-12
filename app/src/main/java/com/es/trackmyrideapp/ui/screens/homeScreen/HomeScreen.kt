package com.es.trackmyrideapp.ui.screens.homeScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.ui.permissions.AppPermission
import com.es.trackmyrideapp.ui.permissions.BlockedDialog
import com.es.trackmyrideapp.ui.permissions.RationaleDialog
import com.es.trackmyrideapp.ui.permissions.rememberPermissionHandler
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
){
    val homeViewModel: HomeViewModel = hiltViewModel()
    val sessionViewModel: SessionViewModel = LocalSessionViewModel.current
    val tracking = homeViewModel.trackingState.value
    val uiMessage by homeViewModel.uiMessage.collectAsState()
    val bottomPadding = if (tracking) 108.dp else 32.dp

    val animationFinished by homeViewModel.animationFinished.collectAsState()
    val shouldWaitForAnimation by homeViewModel.shouldWaitForAnimation.collectAsState()

    // Manejador de permisos de ubicación
    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.Location
    )

    val isMapReady = remember { mutableStateOf(false) }
    val currentLocation by homeViewModel.currentLocation.collectAsState()

    // Cargar mientras no hay permisos o ubicación
    val showLoading = derivedStateOf {
        !permissionState.isGranted || currentLocation == null
    }
    var showPermDialog by remember { mutableStateOf(false) }
    var readyToRequestPermission by remember { mutableStateOf(false) }


    // Retrasa la petición de permiso para evitar hacerlo inmediatamente al abrir
    LaunchedEffect(Unit) {
        if (!permissionState.isGranted &&
            !permissionState.shouldShowBlockedDialog &&
            !permissionState.shouldShowRationaleDialog
        ) {
            delay(2000L)
            readyToRequestPermission = true
        }
    }


    // Solo si está listo y permiso no está concedido, pide el permiso
    if (readyToRequestPermission && !permissionState.isGranted) {
        LaunchedEffect(Unit) {
            requestPermission()
        }
    }

    // Si tenemos permiso, obtener ubicacion
    LaunchedEffect(permissionState.isGranted){
        Log.d("Flujotest", "Launchedeffct de permission isgranted. Isgranted?: ${permissionState.isGranted}")
        if (permissionState.isGranted){
            Log.d("Flujotest", "Launchedeffct de permission isgranted. Dentro de que ya es granted")
            //delay(200)
            showPermDialog = false
            homeViewModel.getLastKnownLocation()
        }
    }

    val readyToShowMap = remember { mutableStateOf(false) }

    // Solo mostrar el mapa cuando se tiene permiso y la ubicación actual
    LaunchedEffect(permissionState.isGranted, currentLocation) {
        if (permissionState.isGranted && currentLocation != null) {
            delay(300)
            readyToShowMap.value = true
        }else {
            readyToShowMap.value = false
        }
    }



    // Snackbar msg
    LaunchedEffect(uiMessage, animationFinished, shouldWaitForAnimation) {
        uiMessage?.let { message ->
            if (!shouldWaitForAnimation) {
                // Mostrar snackbar inmediatamente
                sessionViewModel.showSnackbar(
                    UiSnackbar(
                        message = message.message,
                        messageType = message.type,
                        withDismissAction = true
                    )
                )
                homeViewModel.consumeUiMessage()
            } else if (animationFinished) {
                // Mostrar snackbar después de que la animación termine
                sessionViewModel.showSnackbar(
                    UiSnackbar(
                        message = message.message,
                        messageType = message.type,
                        withDismissAction = true
                    )
                )
                homeViewModel.consumeUiMessage()
                homeViewModel.resetAnimationFinishedFlag()
            }
            // Si debería esperar animación pero esta no ha terminado, no hacer nada aún
        }
    }


    // Logs depuracion
    SideEffect  {
        Log.d("Flujotest", "Info. Showloading Valor: ${showLoading.value}, location: $currentLocation, isMapReady: ${isMapReady.value}. Permissionstate granted?: ${permissionState.isGranted}")
    }

    LaunchedEffect(permissionState) {
        Log.d("Flujotest", "PERMISSION STATE: Granted: ${permissionState.isGranted}, Rationale: ${permissionState.shouldShowRationaleDialog}, Blocked: ${permissionState.shouldShowBlockedDialog}")
    }


    // Diálogo si el permiso fue bloqueado
    if (showPermDialog) {
        BlockedDialog(
            onSettings = {
                // Cuando el usuario dice "Already Enabled", intentamos volver a pedir permiso
                requestPermission()
            },
            onCheckPermission = {
                // Al pulsar "Already Enabled", vuelves a verificar permisos
                requestPermission()
            }
        )
    }


    // Flujo principal de UI basado en el estado de permisos y carga
    when {
        permissionState.shouldShowRationaleDialog -> {
            RationaleDialog(
                onRetry = requestPermission,
                msg = "This permission is needed to continue using this App."
            )
        }

        permissionState.shouldShowSystemDialog -> {
            LaunchedEffect(Unit) {
                requestPermission()
            }
        }

        showLoading.value -> {
            sessionViewModel.showLoading()
        }

        readyToShowMap.value -> {
            Log.d("Flujotest", "Entrando a mostrar mainccontent en el when de homesscreen")
            sessionViewModel.hideLoading()

            MainContent(
                modifier,
                homeViewModel,
                bottomPadding,
                onMapLoaded = {
                    isMapReady.value = true
                    sessionViewModel.hideLoading()
                }
            )
        }
    }

    // Controlar apertura del diálogo de bloqueo
    LaunchedEffect(permissionState.shouldShowBlockedDialog) {
        showPermDialog = permissionState.shouldShowBlockedDialog
    }
}


@Composable
fun MainContent(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    bottomPadding: Dp,
    onMapLoaded: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        MapScreen(homeViewModel, onMapLoaded)

        TrackingButton(
            homeViewModel = homeViewModel,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = bottomPadding, start = 16.dp)
                .shadow(8.dp, CircleShape)
        )
    }
}

