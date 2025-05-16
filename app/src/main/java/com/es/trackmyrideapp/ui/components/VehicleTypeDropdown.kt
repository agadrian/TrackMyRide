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
        DropdownOption(VehicleType.CAR, "Car", Icons.Default.DirectionsCar),
        DropdownOption(VehicleType.MOTORCYCLE, "Motorcyle", Icons.Default.TwoWheeler),
        DropdownOption(VehicleType.BIKE, "Bike", Icons.AutoMirrored.Filled.DirectionsBike)
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