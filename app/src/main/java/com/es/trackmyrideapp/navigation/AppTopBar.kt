package com.es.trackmyrideapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.ButtonColors
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
import com.es.trackmyrideapp.ui.SessionViewModel
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
    onBackClicked: () -> Unit,
    drawerState: DrawerState,
    navigateToHistoryClicked: () -> Unit,
    navigateToHomeClicked: () -> Unit,
    onMapTypeChanged: (MapType) -> Unit,
    onVehicleTypeChanged: (VehicleType) -> Unit,
    currentMapType: MapType,
    currentVehicleType: VehicleType,
){

    var showMapTypeMenu by remember { mutableStateOf(false) }
    var showVehicleTypeMenu by remember { mutableStateOf(false) }
    val selectedVehicle = LocalSessionViewModel.current.vehicleType.collectAsState().value

    val title = when (currentDestination) {
        Home::class.qualifiedName -> "Home"
        Profile::class.qualifiedName -> "My Profile"
        Premium::class.qualifiedName -> "Premium"
        AboutUs::class.qualifiedName -> "About Us"
        Vehicles::class.qualifiedName -> "My Vehicles"
        RoutesHistory::class.qualifiedName -> "Routes History"
        RouteDetails::class.qualifiedName -> "Route Details"
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
                // Mostrar flecha para volver o el icono para abrir Drawer
                if (showBackButton){
                    IconButton(
                        onClick = onBackClicked
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }else{
                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            },
            actions = {
                when (currentDestination) {
                    Home::class.qualifiedName -> {
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
                                        VehicleType.Car -> Icons.Default.DirectionsCar
                                        VehicleType.MotorCycle -> Icons.Default.TwoWheeler
                                        VehicleType.Bike -> Icons.AutoMirrored.Filled.DirectionsBike
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
                                                VehicleType.Car -> "Car"
                                                VehicleType.MotorCycle -> "Motorcycle"
                                                VehicleType.Bike -> "Bike"
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

                    RoutesHistory::class.qualifiedName -> {
                        IconButton(
                            onClick = navigateToHomeClicked
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Map")
                        }
                    }
                }
            }
        )

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}
