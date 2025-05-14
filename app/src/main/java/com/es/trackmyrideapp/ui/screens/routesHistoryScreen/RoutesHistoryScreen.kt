package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.ui.components.VehicleFilter
import com.es.trackmyrideapp.ui.components.VehicleFilterSelector
import com.es.trackmyrideapp.ui.components.VehicleType
import okhttp3.internal.concurrent.formatDuration


data class RouteWithVehicleType(
    val route: Route,
    val vehicleType: VehicleType
)

@Composable
fun RoutesHistoryScreen(
    onViewDetailsClicked: () -> Unit,
    modifier: Modifier
){
    val routesHistoryViewModel: RoutesHistoryViewModel = hiltViewModel()
    var selectedFilter by remember { mutableStateOf<VehicleFilter>(VehicleFilter.All) }

    val scrollState = rememberScrollState()

    LaunchedEffect(selectedFilter) {
        scrollState.animateScrollTo(0)
    }

    var routeToDelete by remember { mutableStateOf<Route?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val allRoutes by routesHistoryViewModel.routes.collectAsState()

    val filteredRoutes = when (selectedFilter) {
        is VehicleFilter.All -> allRoutes
        is VehicleFilter.Type -> allRoutes.filter {
            it.vehicleType == (selectedFilter as VehicleFilter.Type).type
        }
    }


    // Cuando tenga el viewmodel usar el stateflow:

    // En el viewmodel:
    // private val _routes = MutableStateFlow<List<Route>>(emptyList())
    // val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    // Aqui:
    // val allRoutes by viewModel.routes.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            //.verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VehicleFilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it } ,
            paddingScreen = 60.dp,
            true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar las rutas filtradas
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            filteredRoutes.forEach { item ->

                val route = item.route

                val title = route.name
                val distance = "${route.distanceKm} Km"
                val duration = formatDuration(route.movingTimeSec)
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
                    onViewDetailsClicked = onViewDetailsClicked
                )

                Spacer(modifier = Modifier.height(24.dp))
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
                        // TODO: Aquí llamar a una función del ViewModel para borrar
                        // viewModel.deleteRoute(routeToDelete!!.id)
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


        /* Cuando tenga el viewmodel, para mostrar circulo de carga
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text("Error: ${uiState.error}")
            filteredRoutes.isEmpty() -> Text("No hay rutas para este filtro")
            else -> {
                Column {
                    filteredRoutes.forEach { route ->
                        Text(route.name)
                        // O poné acá un Card si querés que se vea más pro
                    }
                }
            }
        }

         */
    }
}





@Preview
@Composable
fun PDF(
){
    RoutesHistoryScreen({},Modifier)
}