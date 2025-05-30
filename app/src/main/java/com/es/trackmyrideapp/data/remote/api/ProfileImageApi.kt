package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequest
import com.es.trackmyrideapp.data.remote.dto.ProfileImageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileImageApi {

    @PUT("/users/profile-image")
    suspend fun uploadImage(
        @Body request: ProfileImageRequest
    ): ProfileImageResponse

    @GET("/users/profile-image")
    suspend fun getImage(): ProfileImageResponse

    @DELETE("/users/profile-image")
    suspend fun deleteImage(): Response<Unit>
}