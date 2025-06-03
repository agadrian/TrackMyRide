package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIcon


@Composable
fun AddPinDialog(
    title: String,
    titleError: String?,
    onTitleChange: (String) -> Unit,
    description: String,
    descriptionError: String?,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                "Add new Pin",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                CustomTextFieldWithoutIcon(
                    label = "Title",
                    value = title,
                    onValueChange = onTitleChange,
                    isError = titleError != null,
                    errorMessage = titleError
                )

                CustomTextFieldWithoutIcon(
                    label = "Description",
                    value = description,
                    onValueChange = onDescriptionChange,
                    isError = descriptionError != null,
                    errorMessage = descriptionError
                )
            }
        }
    )
}