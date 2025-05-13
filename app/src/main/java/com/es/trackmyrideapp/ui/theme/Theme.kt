package com.es.trackmyrideapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

/*
primary = Color(0xFF6200EE),     // Color principal (ej: botones, barra de app)
    onPrimary = Color(0xFFFFFFFF),   // Color del texto o íconos sobre `primary`
    primaryContainer = Color(0xFFBB86FC), // Color del contenedor principal
    onPrimaryContainer = Color(0xFF3700B3),

    secondary = Color(0xFF03DAC6),   // Color secundario (ej: botones secundarios)
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color(0xFFBB86FC),

    background = Color(0xFFFFFFFF),  // Color de fondo general
    onBackground = Color(0xFF000000),// Color del contenido sobre el fondo

    surface = Color(0xFFFFFFFF),     // Color de fondo de tarjetas, diálogos, etc.
    onSurface = Color(0xFF000000),   // Color del texto en superficies

    error = Color(0xFFB00020),       // Color para errores (ej: mensajes de error)
    onError = Color(0xFFFFFFFF),     // Color del texto en áreas de error
 */

private val DarkColorScheme = darkColorScheme(
    /*
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
     */

    background = black,
    onBackground = white,
    primary = green,
    secondary = grayFontDark,
    surface = surfaceDark,
    surfaceVariant = surfaceVariantDark,
    surfaceTint = surfaceVariantDark


)

private val LightColorScheme = lightColorScheme(
    /*
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
     */

    background = white,
    onBackground = black,
    primary = green,
    secondary = grayFont,
    surface = surfaceLight,
    surfaceVariant = surfaceVariantLight,
    surfaceTint = surfaceVariantLight // Mismo color para que no cambie al tener elevacion


)


@Composable
fun TrackMyRideAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


/* Default function
@Composable
fun TrackMyRideAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
*/
