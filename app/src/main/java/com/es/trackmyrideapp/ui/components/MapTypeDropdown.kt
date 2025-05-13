package com.es.trackmyrideapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.MapType

@Composable
fun MapTypeDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    currentMapType: MapType,
    onMapTypeSelected: (MapType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        DropdownOption(MapType.NORMAL, "Normal", Icons.Default.Map),
        DropdownOption(MapType.SATELLITE, "Satellite", Icons.Default.Satellite),
        DropdownOption(MapType.HYBRID, "Hybrid", Icons.Default.Layers),
        DropdownOption(MapType.TERRAIN, "Terrain", Icons.Default.Terrain)
    )

    SelectableDropdown(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        options = options,
        selectedOption = currentMapType,
        onOptionSelected = onMapTypeSelected,
        modifier = modifier
    )
}