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
