package com.es.trackmyrideapp.data.remote

import android.util.Log
import com.es.trackmyrideapp.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authPreferences: AuthPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Estas rutas no requieren autenticación, se permite la petición sin token
        if (request.url.encodedPath.endsWith("/auth/login")
            || request.url.encodedPath.endsWith("/auth/register")
            || request.url.encodedPath.endsWith("/auth/refresh")
            || request.url.encodedPath.endsWith("/users/setPremium")
            ) {
            return chain.proceed(request)
        }

        val jwtToken = authPreferences.getJwtToken()

        Log.d("AuthInterceptor", "Interceptando petición a ${request.url}, token: $jwtToken")

        return if (!jwtToken.isNullOrBlank()) {
            // Si hay token, se añade al header Authorization
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request) // Sin token, continuar sin Authorization
        }
    }
}