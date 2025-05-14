package com.es.trackmyrideapp.data.remote

import com.es.trackmyrideapp.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authPreferences: AuthPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Recuperar el token JWT almacenado
        val jwtToken = authPreferences.getJwtToken()

        // Si el token existe, añadirlo al encabezado de la solicitud
        val requestBuilder = chain.request().newBuilder()

        jwtToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        // Continuar con la solicitud
        return chain.proceed(requestBuilder.build())
    }
}