package com.es.trackmyrideapp.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.ui.permissions.AppPermission
import com.es.trackmyrideapp.ui.permissions.BlockedDialog
import com.es.trackmyrideapp.ui.permissions.RationaleDialog
import com.es.trackmyrideapp.ui.permissions.rememberPermissionHandler

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
){
    val homeViewModel: HomeViewModel = hiltViewModel()
    val tracking = homeViewModel.trackingState.value
    val uiMessage = homeViewModel.uiMessage.value
    val bottomPadding = if (tracking) 108.dp else 32.dp

    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.Location
    )


    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg.message)
            homeViewModel.clearUiMessage()
        }
    }


    when {
        permissionState.isGranted -> {
            Box(
                modifier = modifier.fillMaxSize().background(Color.LightGray), // TODO()
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