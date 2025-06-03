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