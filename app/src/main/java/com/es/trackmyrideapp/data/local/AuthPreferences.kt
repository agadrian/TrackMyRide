package com.es.trackmyrideapp.data.local

import android.content.Context

class AuthPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // === JWT Token ===

    // Guardar el token JWT
    fun setJwtToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    // Recuperar el token JWT
    fun getJwtToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    // Limpiar el token JWT
    fun clearJwtToken() {
        prefs.edit().remove("jwt_token").apply()
    }

    // === Refresh Token ===

    fun setRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun clearRefreshToken() {
        prefs.edit().remove("refresh_token").apply()
    }


    // En caso de tener que limpiar todos los tokens
    fun clearAllTokens() {
        prefs.edit()
            .remove("jwt_token")
            .remove("refresh_token")
            .apply()
    }
}