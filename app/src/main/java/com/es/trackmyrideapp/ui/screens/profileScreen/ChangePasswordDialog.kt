package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChangePasswordDialog(
    showDialog: Boolean,
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    currentPasswordVisible: Boolean,
    newPasswordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    currentPasswordError: String? = null,
    newPasswordError: String? = null,
    confirmPasswordError: String? = null,
    generalError: String? = null
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Change Password") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomPasswordField(
                    label = "Current Password",
                    password = currentPassword,
                    onPasswordChanged = onCurrentPasswordChanged,
                    passwordVisible = currentPasswordVisible,
                    onToggleVisibility = onToggleCurrentPasswordVisibility,
                    error = currentPasswordError
                )

                CustomPasswordField(
                    label = "New Password",
                    password = newPassword,
                    onPasswordChanged = onNewPasswordChanged,
                    passwordVisible = newPasswordVisible,
                    onToggleVisibility = onToggleNewPasswordVisibility,
                    error = newPasswordError
                )

                CustomPasswordField(
                    label = "Confirm New Password",
                    password = confirmPassword,
                    onPasswordChanged = onConfirmPasswordChanged,
                    passwordVisible = confirmPasswordVisible,
                    onToggleVisibility = onToggleConfirmPasswordVisibility,
                    error = confirmPasswordError
                )

                if (generalError != null) {
                    Text(
                        text = generalError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun CustomPasswordField(
    label: String,
    password: String,
    onPasswordChanged: (String) -> Unit,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?
) {
    Column {
        TextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text(label, fontSize = 14.sp) },
            isError = error != null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}