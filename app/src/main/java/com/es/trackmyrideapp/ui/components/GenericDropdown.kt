package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DropdownOption<T>(
    val value: T,
    val label: String,
    val icon: ImageVector? = null
)

@Composable
fun <T> SelectableDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<DropdownOption<T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.label) },
                onClick = {
                    onOptionSelected(option.value)
                    onDismissRequest()
                },
                leadingIcon = option.icon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.background(
                    if (option.value == selectedOption) selectedColor else Color.Transparent
                )
            )
        }
    }
}

/*
@Composable
fun MapTypeDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    currentMapType: MapType, // Añadimos el tipo actual
    onMapTypeSelected: (MapType) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        // Función para determinar si un ítem está seleccionado
        fun isSelected(type: MapType) = currentMapType == type

        // Color para ítems seleccionados
        val selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

        // Opciones del menú
        DropdownMenuItem(
            text = { Text("Normal") },
            onClick = {
                onMapTypeSelected(MapType.NORMAL)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null
                )
            },
            modifier = Modifier.background(
                if (isSelected(MapType.NORMAL)) selectedColor else Color.Transparent
            )
        )
        DropdownMenuItem(
            text = { Text("Satellite") },
            onClick = {
                onMapTypeSelected(MapType.SATELLITE)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Satellite,
                    contentDescription = null
                )
            },
            modifier = Modifier.background(
                if (isSelected(MapType.SATELLITE)) selectedColor else Color.Transparent
            )
        )
        DropdownMenuItem(
            text = { Text("Hybrid") },
            onClick = {
                onMapTypeSelected(MapType.HYBRID)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Layers,
                    contentDescription = null
                )
            },
            modifier = Modifier.background(
                if (isSelected(MapType.HYBRID)) selectedColor else Color.Transparent
            )
        )
        DropdownMenuItem(
            text = { Text("Terrain") },
            onClick = {
                onMapTypeSelected(MapType.TERRAIN)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Terrain,
                    contentDescription = null
                )
            },
            modifier = Modifier.background(
                if (isSelected(MapType.TERRAIN)) selectedColor else Color.Transparent
            )
        )
    }
}
*/