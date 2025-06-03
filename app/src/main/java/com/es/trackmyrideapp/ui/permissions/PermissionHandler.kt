package com.es.trackmyrideapp.ui.permissions

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionHandler(
    permission: AppPermission
): Pair<PermissionRequestState, () -> Unit> {
    val context = LocalContext.current
    var state by remember { mutableStateOf(PermissionRequestState()) }

    // Launcher que se utiliza para pedir múltiples permisos del sistema.
    // Este se dispara cuando se quiere mostrar el diálogo de permisos (lo hace el LaunchedEffect).
    // El bloque de callback (lambda) recibe un `result` con el resultado de cada permiso pedido.
    //
    // Dentro del callback:
    // - Se comprueba si todos los permisos fueron concedidos (`allGranted`)
    // - Se detecta si algún permiso fue denegado permanentemente (`permanentlyDenied`),
    //   usando `shouldShowRequestPermissionRationale`
    // - Según el resultado, se actualiza el estado `state`, que luego puede ser usado por la UI
    //   para mostrar un diálogo de "rationale", "bloqueado", o simplemente continuar si está concedido.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Log.d("PermissionHandler", "Result of permission request: $result")
        val allGranted = result.values.all { it }

        // Detectar si fue denegado dos veces
        val permanentlyDenied = result.entries.any { (perm, granted) ->
            val deniedPermanently =
                !granted &&
                        ContextCompat.checkSelfPermission(context, perm) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, perm)

            Log.d("PermissionHandler", "Permission $perm granted=$granted, permanentlyDenied=$deniedPermanently")
            deniedPermanently
        }


        state = when {
            allGranted -> {
                Log.d("PermissionHandler", "All permissions granted.")
                PermissionRequestState(isGranted = true)
            }

            permanentlyDenied -> {
                Log.d("PermissionHandler", "Permission permanently denied.")
                PermissionRequestState(shouldShowBlockedDialog = true)
            }


            else -> {
                Log.d("PermissionHandler", "Permission denied with rationale needed.")
                PermissionRequestState(shouldShowRationaleDialog = true)
            }
        }
    }

    // Comprobacion manual del estado. Revisa si tiene permisos y cambia el estado dependiendo de el resultado de esta comprobacion.
    val launchRequest = {
        val allGranted = permission.permissions.all {
            ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        Log.d("PermissionHandler", "Launch permission check. All granted: $allGranted")

        if (allGranted) {
            Log.d("PermissionHandler", "Permission already granted.")
            state = PermissionRequestState(isGranted = true)
           // onPermissionGranted()
        } else {
            Log.d("PermissionHandler", "Launching permission request dialog.")
            state = PermissionRequestState(shouldShowSystemDialog = true)
        }
    }

    // Lanza el dialogo de permisos si es necesario
    if (state.shouldShowSystemDialog) {
        Log.d("PermissionHandler", "Launching system permission dialog with: ${permission.permissions}")
        LaunchedEffect(Unit) {
            launcher.launch(permission.permissions.toTypedArray())
            state = state.copy(shouldShowSystemDialog = false)
        }
    }

    return Pair(state, launchRequest)
}
