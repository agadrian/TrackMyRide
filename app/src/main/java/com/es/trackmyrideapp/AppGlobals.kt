package com.es.trackmyrideapp

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel

// Variables globales
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
}

object RoutesHistoryConstants {
    const val MAX_ROUTES_PREMIUM = 100
    const val MAX_ROUTES_NO_PREMIUM = 4
}

object HomeScreenConstants {
    const val MIN_ROUTE_POINTS = 3
    const val MIN_DURATION_MILLIS = 10_000L
}