package com.es.trackmyrideapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.screens.vehiclesScreen.IconSelectorBar

@Composable
fun VehicleFilterSelector(
    selectedFilter: VehicleFilter,
    onFilterSelected: (VehicleFilter) -> Unit,
    paddingScreen: Dp,
    showAll: Boolean = false
) {

    // Mostrar el "All o no"
    val filterOptions = buildList {
        if (showAll) add(VehicleFilter.All)
        add(VehicleFilter.Type(VehicleType.Car))
        add(VehicleFilter.Type(VehicleType.MotorCycle))
        add(VehicleFilter.Type(VehicleType.Bike))
    }

    IconSelectorBar(
        items = filterOptions,
        selectedItem = selectedFilter,
        onItemSelected = onFilterSelected,
        itemIcon = { filter ->
            val iconTint = if (filter == selectedFilter){
                colorResource(R.color.black)
            }else{
                MaterialTheme.colorScheme.onBackground
            }

            when (filter) {
                is VehicleFilter.All -> Icon(
                    painter = painterResource(R.drawable.all_vehicles),
                    contentDescription = "All",
                    tint = iconTint
                )
                is VehicleFilter.Type -> when (filter.type) {
                    VehicleType.Car -> Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = iconTint
                    )
                    VehicleType.MotorCycle -> Icon(
                        painter = painterResource(R.drawable.motocicleta),
                        contentDescription = "Motorcycle",
                        tint = iconTint
                    )
                    VehicleType.Bike -> Icon(
                        Icons.Default.PedalBike,
                        contentDescription = "Bike",
                        tint = iconTint
                    )
                }
            }
        },
        paddingScreen = paddingScreen
    )
}