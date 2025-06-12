package com.es.trackmyrideapp.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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



    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.Location
    )




//    LaunchedEffect(permissionState.isGranted) {
//        Log.d("PERMISO", "permissionState: isGranted=${permissionState.isGranted}, rationale=${permissionState.shouldShowRationaleDialog}, blocked=${permissionState.shouldShowBlockedDialog}")
//        if (permissionState.isGranted && homeViewModel.currentLocation.value == null) {
//            homeViewModel.getLastKnownLocation()
//        }
//    }

    LaunchedEffect(permissionState.isGranted){
        if (permissionState.isGranted){
            delay(200)
            homeViewModel.getLastKnownLocation()
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





    when {
        permissionState.isGranted -> {
            MainContent(modifier, homeViewModel, bottomPadding)
        }

        permissionState.shouldShowRationaleDialog -> {
            RationaleDialog(
                onRetry = requestPermission,
                msg = "This permission is needed to continue using this App."
            )
        }

        permissionState.shouldShowBlockedDialog -> {
            BlockedDialog(onSettings = requestPermission)
        }

        else -> {
            LaunchedEffect(Unit) {
                requestPermission()
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    bottomPadding: Dp
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.LightGray)
    ) {
        MapScreen(homeViewModel)

        TrackingButton(
            homeViewModel = homeViewModel,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = bottomPadding, start = 16.dp)
                .shadow(8.dp, CircleShape)
        )
    }
}