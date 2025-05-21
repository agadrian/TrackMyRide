package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.AuthResponseDTO
import com.es.trackmyrideapp.data.remote.dto.RefreshTokenRequestDTO
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Header("Authorization") bearerToken: String,
        @Body registrationDTO: UserRegistrationDTO
    ): Response<AuthResponseDTO>

    @POST("auth/login")
    suspend fun login(
        @Header("Authorization") bearerToken: String
    ): Response<AuthResponseDTO>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDTO): Response<AuthResponseDTO>

    @GET("auth/validate")
    suspend fun validateToken(@Header("Authorization") authHeader: String): Response<Map<String, String>>
}

