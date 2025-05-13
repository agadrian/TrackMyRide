package com.es.trackmyrideapp.ui.theme

import androidx.compose.ui.graphics.Color

/*
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
*/

val white = hexToColor("#FFFFFF")
val black = hexToColor("#000000")
val grayFont = hexToColor("#505050")

val grayFontDark = hexToColor("#BFBFBF")

val green = hexToColor("#8BBE8C")

val surfaceDark = hexToColor("#121212")
val surfaceVariantDark = hexToColor("#1E1E1E")

val surfaceLight = hexToColor("#FFFFFF")
val surfaceVariantLight = hexToColor("#F6F6F6")

val greenBack = hexToColor("#97D099")
val orangeButton = hexToColor("#F59E0B")
val orangeOnBackground = hexToColor("#D97706")
val orangeBackground = hexToColor("#FEF3C7")
val redButton = hexToColor("#D40000")




/**
 * Funcion para convertir colores 'HEX' a 'Color'
 */
fun hexToColor(hex: String): Color {
    val color = if (hex.startsWith("#")) {
        hex.substring(1)
    } else {
        hex
    }

    val colorValue = if (color.length == 6) "FF$color" else color // Prevenir colores RGBA

    // Hex -> Int -> Color
    return Color(android.graphics.Color.parseColor("#$colorValue"))
}