package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.AuthResponseDTO
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import retrofit2.Response
import retrofit2.http.Body
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
}