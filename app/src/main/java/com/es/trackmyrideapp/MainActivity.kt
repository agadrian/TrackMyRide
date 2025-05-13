package com.es.trackmyrideapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.es.trackmyrideapp.data.local.ThemePreferences
import com.es.trackmyrideapp.navigation.Home
import com.es.trackmyrideapp.navigation.Login
import com.es.trackmyrideapp.navigation.NavigationWrapper
import com.es.trackmyrideapp.ui.AuthState
import com.es.trackmyrideapp.ui.SessionViewModel
import com.es.trackmyrideapp.ui.theme.TrackMyRideAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Variables globales
val LocalIsDarkTheme = compositionLocalOf { false}
val LocalSessionViewModel = compositionLocalOf<SessionViewModel> { error("No SessionViewModel found") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()
    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().setKeepOnScreenCondition {
            sessionViewModel.authState.value == AuthState.Loading
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Dark / Light theme
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(
                    if (themePreferences.hasUserSetTheme()) {
                        themePreferences.isDarkThemeEnabled()
                    } else {
                        systemDark
                    }
                )
            }

            val authState by sessionViewModel.authState.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalIsDarkTheme provides isDarkTheme,
                LocalSessionViewModel provides sessionViewModel
            ) {
                TrackMyRideAppTheme(darkTheme = isDarkTheme) {
                    NavigationWrapper(
                        isDarkTheme = isDarkTheme,
                        onThemeChanged = { isDarkTheme = it },
                        startDestination = if (authState == AuthState.Authenticated) {
                            Home::class.qualifiedName!!
                        } else {
                            Login::class.qualifiedName!!
                        }
                    )
                }
            }
        }
    }
}


