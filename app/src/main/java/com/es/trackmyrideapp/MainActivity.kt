package com.es.trackmyrideapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.es.trackmyrideapp.core.states.AuthState
import com.es.trackmyrideapp.data.local.ThemePreferences
import com.es.trackmyrideapp.ui.navigation.AdminScreen
import com.es.trackmyrideapp.ui.navigation.Home
import com.es.trackmyrideapp.ui.navigation.Login
import com.es.trackmyrideapp.ui.navigation.NavigationWrapper
import com.es.trackmyrideapp.ui.theme.TrackMyRideAppTheme
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * MainActivity:
 * Actividad principal de la aplicación, configurada para usar Hilt (@AndroidEntryPoint).
 * Se encarga de manejar el estado de autenticación y el tema (claro/oscuro) de la app.
 *
 * - Inyecta ThemePreferences para gestionar las preferencias de tema.
 * - Obtiene SessionViewModel para controlar estado de sesión y autenticación.
 * - Usa SplashScreen que se mantiene mientras el estado de autenticación está en "Loading".
 * - Habilita el modo edge-to-edge para UI inmersiva.
 * - En Compose, configura el tema basado en la preferencia del usuario o el tema del sistema.
 * - Observa el estado de autenticación y rol de usuario desde el ViewModel.
 * - Según el estado y rol, define la pantalla inicial:
 *    * Si no está autenticado: pantalla de Login.
 *    * Si es ADMIN: pantalla AdminScreen.
 *    * Si es usuario normal: pantalla Home.
 * - Proporciona CompositionLocals para pasar tema y sesión a los composables hijos.
 * - Muestra un indicador de carga si el estado sigue siendo Loading (por seguridad).
 */
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
            val userRole by sessionViewModel.userRole.collectAsStateWithLifecycle( null)

            Log.d("FlujoTest", "userRole: $userRole .")

            CompositionLocalProvider(
                LocalIsDarkTheme provides isDarkTheme,
                LocalSessionViewModel provides sessionViewModel
            ) {
                TrackMyRideAppTheme(darkTheme = isDarkTheme) {
                    if (authState != AuthState.Loading) {
                        NavigationWrapper(
                            isDarkTheme = isDarkTheme,
                            onThemeChanged = { isDarkTheme = it },
                            startDestination = when {
                                authState != AuthState.Authenticated -> {
                                    Log.d("FlujoTest", "userRole: $userRole . Login screen deberian aparecer")
                                    Login::class.qualifiedName!!
                                }
                                userRole == "ADMIN" -> {
                                    Log.d(
                                        "FlujoTest",
                                        "userRole: $userRole . Adminn screen deberian aparecer"
                                    )
                                    AdminScreen::class.qualifiedName!!
                                }

                                else -> Home::class.qualifiedName!!
                            }
                        )
                    }else{
                        // Por si llegara a apsar algo (NO DEBERIA)
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}


