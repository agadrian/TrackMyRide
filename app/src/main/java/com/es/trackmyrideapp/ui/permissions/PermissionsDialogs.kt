package com.es.trackmyrideapp.ui.permissions

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun RationaleDialog(
    onRetry: () -> Unit,
    msg: String
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        },
        title = { Text("Permission Required") },
        text = { Text(msg) }
    )
}

@Composable
fun BlockedDialog(
    onSettings: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        confirmButton = {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            Button(onClick = onSettings) {
                Text("Already Enabled")
            }
        },
        title = { Text("Permission Blocked") },
        text = { Text("You need to manually enable this permission in system settings to continue using this App.") }
    )
}



@Composable
fun ClosableBlockedDialog(
    onDismiss: () -> Unit,
    onResumeCheck: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasLaunchedSettings by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME && hasLaunchedSettings) {
                    onResumeCheck()
                    hasLaunchedSettings = false
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Blocked") },
        text = {
            Text("To attach images, you need to manually enable this permission in the system settings.")
        },
        confirmButton = {
            Button(onClick = {
                hasLaunchedSettings = true
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}