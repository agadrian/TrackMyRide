package com.es.trackmyrideapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.ui.components.MapTypeDropdown
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.ui.components.VehicleTypeDropdown
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentDestination: String?,
    scope: CoroutineScope,
    showBackButton: Boolean,
    showDrawerMenuButton: Boolean,
    onBackClicked: () -> Unit,
    drawerState: DrawerState,
    navigateToHistoryClicked: () -> Unit,
    navigateToHomeClicked: () -> Unit,
    onMapTypeChanged: (MapType) -> Unit,
    onVehicleTypeChanged: (VehicleType) -> Unit,
    currentMapType: MapType,
    currentVehicleType: VehicleType,
    onLogoutAdminClicked: () -> Unit,
    onRefreshAdminScreen: () -> Unit
){
    val sessionViewModel = LocalSessionViewModel.current
    var showMapTypeMenu by remember { mutableStateOf(false) }
    var showVehicleTypeMenu by remember { mutableStateOf(false) }
    val selectedVehicle = sessionViewModel.selectedVehicle.collectAsState().value
    val isEditingDetails by sessionViewModel.isEditingRouteDetails.collectAsState()
    val isEditingProfile by sessionViewModel.isEditingProfile.collectAsState()

    val title = when {
        currentDestination == Home::class.qualifiedName -> "Home"
        currentDestination == ForgotPassword::class.qualifiedName -> "Reset Password"
        currentDestination == Profile::class.qualifiedName -> "My Profile"
        currentDestination == Premium::class.qualifiedName -> "Premium"
        currentDestination == AboutUs::class.qualifiedName -> "About Us"
        currentDestination == Vehicles::class.qualifiedName -> "My Vehicles"
        currentDestination == RoutesHistory::class.qualifiedName -> "Routes History"
        currentDestination?.startsWith(RouteDetails::class.qualifiedName ?: "") == true -> "Route Details"
        currentDestination == AdminScreen::class.qualifiedName -> "Admin Screen"
        else -> ""
    }

    val isTransparent = when (currentDestination) {
        Home::class.qualifiedName,
        Profile::class.qualifiedName -> true
        else -> false
    }

    val topBarColor = if (isTransparent) {
        MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.background
    }

    Column{
        TopAppBar(
            title = { Text(
                text = title
            ) },
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = topBarColor,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.statusBarsPadding(), // Pading superior unicamente
            navigationIcon = {
                // Mostrar flecha para volver, el icono para abrir Drawer o nada
                when {
                    showBackButton -> {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                    showDrawerMenuButton -> {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                    else -> {
                        // Nada
                    }
                }
            },
            actions = {
                when  {
                    currentDestination == Home::class.qualifiedName -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Box {
                                TextButton(
                                    onClick = { showVehicleTypeMenu = true },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onBackground
                                    )
                                ) {
                                    val icon = when (selectedVehicle) {
                                        VehicleType.CAR -> Icons.Default.DirectionsCar
                                        VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                                        VehicleType.BIKE -> Icons.AutoMirrored.Filled.DirectionsBike
                                    }

                                    Row(
                                        Modifier
                                            ,
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        Icon(icon, contentDescription = "Vehicle")

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            when (selectedVehicle) {
                                                VehicleType.CAR -> "Car"
                                                VehicleType.MOTORCYCLE -> "Motorcycle"
                                                VehicleType.BIKE -> "Bike"
                                            }
                                        )
                                    }
                                }

                                VehicleTypeDropdown(
                                    expanded = showVehicleTypeMenu,
                                    onDismissRequest = { showVehicleTypeMenu = false },
                                    onVehicleSelected = { vehicleType ->
                                        onVehicleTypeChanged(vehicleType)
                                        showVehicleTypeMenu = false
                                    },
                                    currentVehicle = currentVehicleType
                                )
                            }


                            IconButton(
                                onClick = navigateToHistoryClicked
                            ) {
                                Icon(Icons.Default.History, contentDescription = "History")
                            }

                            // MapType Button
                            Box {
                                IconButton(onClick = { showMapTypeMenu = true }) {
                                    Icon(Icons.Default.Layers, contentDescription = "Layers")
                                }

                                MapTypeDropdown(
                                    expanded = showMapTypeMenu,
                                    onDismissRequest = { showMapTypeMenu = false },
                                    onMapTypeSelected = {
                                        onMapTypeChanged(it)
                                        showMapTypeMenu = false
                                    },
                                    currentMapType = currentMapType
                                )
                            }
                        }
                    }

                    currentDestination == RoutesHistory::class.qualifiedName -> {
                        IconButton(
                            onClick = navigateToHomeClicked
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Map")
                        }
                    }

                    currentDestination?.startsWith(RouteDetails::class.qualifiedName ?: "") == true -> {
                        IconButton(
                            onClick = { sessionViewModel.toggleEditingRouteDetails() }
                        ) {
                            Icon(
                                imageVector = if (isEditingDetails) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (isEditingDetails) "Cancel edition" else "Edit"
                            )
                        }
                    }

                    currentDestination == Profile::class.qualifiedName -> {
                        IconButton(
                            onClick = { sessionViewModel.toggleEditingProfile() }
                        ) {
                            Icon(
                                imageVector = if (isEditingProfile) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (isEditingProfile) "Cancel edition" else "Edit"
                            )
                        }
                    }

                    currentDestination == AdminScreen::class.qualifiedName -> {
                        // Para refresh lista usuarios
                        IconButton(onClick = {
                            onRefreshAdminScreen()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }

                        IconButton(onClick = {
                            onLogoutAdminClicked()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out")
                        }
                    }
                }
            }
        )

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}
