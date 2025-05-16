package com.es.trackmyrideapp.data.local

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONObject

class AuthPreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // === JWT Token ===

    // Guardar el token JWT
    fun setJwtToken(token: String) {
        Log.d("AuthPreferences", "Guardando token JWT: $token")
        prefs.edit().putString("jwt_token", token).apply()
    }

    // Recuperar el token JWT
    fun getJwtToken(): String? {
        Log.d("AuthPreferences", "GetTokenjwt")
        return prefs.getString("jwt_token", null)
    }

    // Limpiar el token JWT
    fun clearJwtToken() {
        Log.d("AuthPreferences", "claeard jwt")
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

    fun getUserIdFromToken(): String? {
        val jwt = getJwtToken() ?: return null
        return try {
            val parts = jwt.split(".")
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            val jsonObject = JSONObject(payload)
            jsonObject.getString("uid") // o "sub" dependiendo de tu JWT
        } catch (e: Exception) {
            null
        }
    }
}