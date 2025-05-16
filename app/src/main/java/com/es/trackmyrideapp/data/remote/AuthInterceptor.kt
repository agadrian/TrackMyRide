package com.es.trackmyrideapp.data.remote

import com.es.trackmyrideapp.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authPreferences: AuthPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.endsWith("/auth/login")
            || request.url.encodedPath.endsWith("/auth/register")
            || request.url.encodedPath.endsWith("/auth/refresh")) {
            return chain.proceed(request)
        }

        val jwtToken = authPreferences.getJwtToken()

        return if (!jwtToken.isNullOrBlank()) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request) // Sin token, continuar sin Authorization
        }
    }
}