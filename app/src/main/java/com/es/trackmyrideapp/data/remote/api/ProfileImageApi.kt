package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequestDTO
import com.es.trackmyrideapp.data.remote.dto.ProfileImageResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileImageApi {

    @PUT("/users/profile-image")
    suspend fun uploadImage(
        @Body request: ProfileImageRequestDTO
    ): ProfileImageResponseDTO

    @GET("/users/profile-image")
    suspend fun getImage(): ProfileImageResponseDTO

    @DELETE("/users/profile-image")
    suspend fun deleteImage(): Response<Unit>
}