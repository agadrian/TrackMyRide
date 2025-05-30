package com.es.trackmyrideapp.ui.screens.vehiclesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.components.VehicleFilter
import com.es.trackmyrideapp.ui.components.VehicleFilterSelector
import com.es.trackmyrideapp.ui.components.VehicleIcon
import com.es.trackmyrideapp.ui.components.VehicleType

@Composable
fun VehiclesScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
){
    val vehicleViewModel: VehiclesViewModel = hiltViewModel()
    val uiMessage by vehicleViewModel.uiMessage.collectAsState()
    val uiState by vehicleViewModel.uiState.collectAsState()
    val selectedFilter by vehicleViewModel.selectedFilter.collectAsState()
    val focusManager = LocalFocusManager.current


    // Mostrar info  en snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message.message,
                withDismissAction = message.type == MessageType.ERROR,
                duration = SnackbarDuration.Short
            )
            vehicleViewModel.consumeUiMessage()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding()
            .clickable(
                // Evita que el click consuma otros eventos
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
    ){

        VehicleFilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = { vehicleViewModel.updateSelectedFilter(it) },
            paddingScreen = 60.dp
        )


        Spacer(modifier = Modifier.height(32.dp))

        when (selectedFilter) {
            is VehicleFilter.Type -> when ((selectedFilter as VehicleFilter.Type).type) {
                VehicleType.CAR -> EngineVehicleScreen(
                    icon = VehicleIcon.Vector(Icons.Default.DirectionsCar),
                    vehicleLabel = "Car",
                    name = vehicleViewModel.name,
                    brand = vehicleViewModel.brand,
                    model = vehicleViewModel.model,
                    year = vehicleViewModel.year,
                    fuelType = vehicleViewModel.fuelType,
                    tankCapacity = vehicleViewModel.tankCapacity,
                    efficiency = vehicleViewModel.efficiency,
                    notes = vehicleViewModel.notes,
                    onNameChange = { vehicleViewModel.updateName(it) },
                    onBrandChange = { vehicleViewModel.updateBrand(it) },
                    onModelChange = { vehicleViewModel.updateModel(it) },
                    onYearChange = { vehicleViewModel.updateYear(it) },
                    onFuelTypeChange = { vehicleViewModel.updateFuelType(it) },
                    onTankCapacityChange = { vehicleViewModel.updateTankCapacity(it) },
                    onEfficiencyChange = { vehicleViewModel.updateEfficiency(it) },
                    onNotesChange = { vehicleViewModel.updateNotes(it) }
                )

                VehicleType.MOTORCYCLE -> EngineVehicleScreen(
                    icon = VehicleIcon.PainterIcon(R.drawable.motocicleta),
                    vehicleLabel = "MotorCycle",
                    name = vehicleViewModel.name,
                    brand = vehicleViewModel.brand,
                    model = vehicleViewModel.model,
                    year = vehicleViewModel.year,
                    fuelType = vehicleViewModel.fuelType,
                    tankCapacity = vehicleViewModel.tankCapacity,
                    efficiency = vehicleViewModel.efficiency,
                    notes = vehicleViewModel.notes,
                    onNameChange = { vehicleViewModel.updateName(it) },
                    onBrandChange = { vehicleViewModel.updateBrand(it) },
                    onModelChange = { vehicleViewModel.updateModel(it) },
                    onYearChange = { vehicleViewModel.updateYear(it) },
                    onFuelTypeChange = { vehicleViewModel.updateFuelType(it) },
                    onTankCapacityChange = { vehicleViewModel.updateTankCapacity(it) },
                    onEfficiencyChange = { vehicleViewModel.updateEfficiency(it) },
                    onNotesChange = { vehicleViewModel.updateNotes(it) }
                )

                VehicleType.BIKE -> BikeScreen(
                    icon = Icons.Default.PedalBike,
                    vehicleLabel = "Bike",
                    name = vehicleViewModel.name,
                    brand = vehicleViewModel.brand,
                    model = vehicleViewModel.model,
                    year = vehicleViewModel.year,
                    bikeType = vehicleViewModel.bikeType,
                    notes = vehicleViewModel.notes,
                    onNameChange = { vehicleViewModel.updateName(it) },
                    onBrandChange = { vehicleViewModel.updateBrand(it) },
                    onModelChange = { vehicleViewModel.updateModel(it) },
                    onYearChange = { vehicleViewModel.updateYear(it) },
                    onBikeTypeChange = { vehicleViewModel.updateBikeType(it) },
                    onNotesChange = { vehicleViewModel.updateNotes(it) }
                )
            }

            VehicleFilter.All -> {
                // Nada en este caso
            }

        }


        Spacer(Modifier.weight(1f))

        Column(
            Modifier.padding(vertical = 32.dp)
        ) {
            CustomButton(
                onclick = {
                    if (vehicleViewModel.validateBeforeSave()) {
                        vehicleViewModel.updateVehicle()
                    }
                },
                text = "Save changes",
                buttonColor = MaterialTheme.colorScheme.primary,
                fontColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    // Indicador de carga
    if (uiState is VehicleUiState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}




