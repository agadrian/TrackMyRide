package com.es.trackmyrideapp

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel

// Constantes globales usadas en la App

val LocalIsDarkTheme = compositionLocalOf { false}
val LocalSessionViewModel = compositionLocalOf<SessionViewModel> { error("No SessionViewModel found") }
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController found")
}

object RouteDetailsConstants {
    const val MAX_NAME_LENGTH = 35
    const val MAX_DESC_LENGTH = 80
    const val MAX_IMAGES_NO_PREMIUM = 3
    const val MAX_IMAGES_PREMIUM = 10
    const val MAX_TITLE_LENGTH = 10
    const val MAX_DESCRIPTION_LENGTH = 15
}

object RoutesHistoryConstants {
    const val MAX_ROUTES_PREMIUM = 100
    const val MAX_ROUTES_NO_PREMIUM = 5
}

object HomeScreenConstants {
    const val MIN_ROUTE_POINTS = 3
    const val MIN_DURATION_MILLIS = 10_000L
    const val DEFAULT_TILT = 0f
    const val TRACKING_TILT = 45f
    const val DEFAULT_BEARING = 0f
    const val MINIMUN_DISTANCE_METERS = 3f
    const val INTERVAL_MILIS_LOCATION_UPDATES = 2000L
    const val MAX_REASONABLE_SPEED_KMH = 299
}

object RegisterScreenConstants {
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_USERNAME_LENGTH = 10
    const val MAX_PHONE_LENGTH = 12
}