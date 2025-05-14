package com.es.trackmyrideapp.data.remote

import android.util.Base64
import android.util.Log
import com.es.trackmyrideapp.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AuthInterceptor(private val authPreferences: AuthPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.endsWith("/auth/login") || request.url.encodedPath.endsWith("/auth/register")) {
            Log.d("AuthInterceptor", "Login o register request")
            return chain.proceed(request)

        }

        val jwtToken = authPreferences.getJwtToken()
        Log.d("AuthInterceptor", "JWT Token: $jwtToken")



        if (jwtToken.isNullOrBlank()) {
            throw IOException("No JWT token found")
        }

//        // Verificar expiración
//        if (isJwtExpired(jwtToken)) {
//            throw IOException("JWT token expired")
//        }

        // Token válido → añadir al header
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $jwtToken")

        return chain.proceed(requestBuilder.build())
    }

    private fun isJwtExpired(jwt: String): Boolean {
        val parts = jwt.split(".")
        if (parts.size != 3) return true

        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            Log.d("AuthInterceptor", "Payload decoded: $payload")
            val json = JSONObject(payload)
            val exp = json.optLong("exp", 0L)
            val now = System.currentTimeMillis() / 1000 // Epoch en segundos
            Log.d("AuthInterceptor", "Token Expiration: $exp")
            Log.d("AuthInterceptor", "Current Time: $now")
            Log.d("AuthInterceptor", "Is Expired: ${now >= exp}")
            now >= exp
        } catch (e: Exception) {
            Log.d("AuthInterceptor", "Error al verificar la expiración del JWT: ${e.message}")
            true // Si hay error, asumimos que está expirado
        }
    }
}
