package com.es.trackmyrideapp.data.local

import android.content.Context

class AuthPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

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
}