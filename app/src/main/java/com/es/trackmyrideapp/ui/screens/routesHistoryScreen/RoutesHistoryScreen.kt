package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.es.trackmyrideapp.LocalNavController
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.components.VehicleFilter
import com.es.trackmyrideapp.ui.components.VehicleFilterSelector
import com.es.trackmyrideapp.ui.navigation.RoutesHistory
import com.es.trackmyrideapp.utils.TimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutesHistoryScreen(
    onViewDetailsClicked: (Long) -> Unit,
    onGetPremiumClicked: () -> Unit,
    modifier: Modifier
){
    val routesHistoryViewModel: RoutesHistoryViewModel = hiltViewModel()
    var selectedFilter by remember { mutableStateOf<VehicleFilter>(VehicleFilter.All) }
    val uiMessage by routesHistoryViewModel.uiMessage.collectAsState()
    val uiState by routesHistoryViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    //  Llamo con launchedeffect a la api para comprobar el premium, y luego miro el estado obtenido
    val sessionViewModel = LocalSessionViewModel.current
    val isPremium by sessionViewModel.isPremium.collectAsState()


    LaunchedEffect(Unit){
        sessionViewModel.checkPremiumStatus()
    }

    LaunchedEffect(selectedFilter) {
        scrollState.animateScrollTo(0)
    }

    val navController = LocalNavController.current
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var lastRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentBackStackEntry.value?.destination?.route) {
        val route = currentBackStackEntry.value?.destination?.route
        if (route == RoutesHistory::class.qualifiedName && route != lastRoute) {
            lastRoute = route
            Log.d("flujotest", "RoutesHistoryScreen: Refreshing. ")
            routesHistoryViewModel.fetchRoutes()
        }else{
            Log.d("flujotest", "RoutesHistoryScreen currentbackstackentry .value;: ${currentBackStackEntry.value?.destination?.route}. ")
        }
    }


    var routeToDelete by remember { mutableStateOf<Route?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val allRoutes by routesHistoryViewModel.routes.collectAsState()
    val filteredRoutes = remember(allRoutes, selectedFilter, isPremium) {
        routesHistoryViewModel.getFilteredRoutes(isPremium, selectedFilter)
    }

    val showButtonGoPremium = routesHistoryViewModel.shouldShowGetPremiumButton(isPremium, selectedFilter)

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
            routesHistoryViewModel.consumeUiMessage()
        }
    }


    // CircularProgessIndicator
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Loading -> sessionViewModel.showLoading()
            else -> sessionViewModel.hideLoading()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VehicleFilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            paddingScreen = 60.dp,
            true
        )

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (filteredRoutes.isEmpty() && uiState !is UiState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "You haven't completed any routes yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }else{
                items(filteredRoutes, key = { it.route.id }) { item ->
                    val route = item.route
                    val title = route.name
                    val distance = "${route.distanceKm} Km"
                    val duration = TimeFormatter.formatSecondsToHhMmSs(route.movingTimeSec)
                    val pace = route.pace?.let { String.format("%.2f", it) + " Km/h" } ?: "-"
                    val date = route.startTime.toLocalDate().toString()

                    RouteCard(
                        tittle = title,
                        duration = duration,
                        distance = distance,
                        pace = pace,
                        date = date,
                        onDeleteClicked = {
                            routeToDelete = route
                            showDeleteDialog = true
                        },
                        onViewDetailsClicked = { onViewDetailsClicked(route.id) },
                        vehicleType = route.vehicleType
                    )
                }

                // Mostrar boton de get premium cuando no lo sea
                if (showButtonGoPremium) {
                    item {
                        CustomButton(
                            onclick = onGetPremiumClicked,
                            buttonColor = colorResource(R.color.orangeButton),
                            fontColor = colorResource(R.color.black),
                            text = "Go Premium to see more"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }


        // Dialog confirmacion para borrar
        if (showDeleteDialog && routeToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Confirmation") },
                text = { Text("Are you sure you want to delete \"${routeToDelete?.name}\"?") },
                confirmButton = {
                    TextButton(onClick = {
                        routesHistoryViewModel.deleteRoute(routeToDelete!!.id)
                        showDeleteDialog = false
                        routeToDelete = null
                    }) {
                        Text(
                            text = "Yes, delete",
                            color = colorResource(R.color.redButton)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        routeToDelete = null
                    }) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
        }
    }
}
