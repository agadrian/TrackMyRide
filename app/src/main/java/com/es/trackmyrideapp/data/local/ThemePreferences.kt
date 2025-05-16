package com.es.trackmyrideapp.data.local

import android.content.Context

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme_enabled", enabled)
            .putBoolean("dark_theme_set", true) // marcamos que el usuario ha elegido
            .apply()
    }

    fun isDarkThemeEnabled(default: Boolean = false): Boolean {
        return prefs.getBoolean("dark_theme_enabled", default)
    }

    fun hasUserSetTheme(): Boolean {
        return prefs.getBoolean("dark_theme_set", false)
    }
}