package com.es.trackmyrideapp.ui.permissions

import android.os.Build

/**
 * Permisos que se usarán a lo largo de la App
 */
sealed class AppPermission {
    abstract val permissions: List<String>

    object Location : AppPermission() {
        override val permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    object ReadImages : AppPermission() {
        override val permissions: List<String>
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
    }
}

