package com.es.trackmyrideapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VehicleTypeDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    currentVehicle: VehicleType,
    onVehicleSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        DropdownOption(VehicleType.Car, "Car", Icons.Default.DirectionsCar),
        DropdownOption(VehicleType.MotorCycle, "Motorcyle", Icons.Default.TwoWheeler),
        DropdownOption(VehicleType.Bike, "Bike", Icons.AutoMirrored.Filled.DirectionsBike)
    )

    SelectableDropdown(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        options = options,
        selectedOption = currentVehicle,
        onOptionSelected = onVehicleSelected,
        modifier = modifier
    )
}