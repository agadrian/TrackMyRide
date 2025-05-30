package com.es.trackmyrideapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.es.trackmyrideapp.R

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
        add(VehicleFilter.Type(VehicleType.CAR))
        add(VehicleFilter.Type(VehicleType.MOTORCYCLE))
        add(VehicleFilter.Type(VehicleType.BIKE))
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
                    VehicleType.CAR -> Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = iconTint
                    )
                    VehicleType.MOTORCYCLE -> Icon(
                        painter = painterResource(R.drawable.motocicleta),
                        contentDescription = "Motorcycle",
                        tint = iconTint
                    )
                    VehicleType.BIKE -> Icon(
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