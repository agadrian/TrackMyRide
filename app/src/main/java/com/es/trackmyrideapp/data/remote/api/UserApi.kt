package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.IsPremiumResponseDTO
import com.es.trackmyrideapp.data.remote.dto.UserResponseDTO
import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    // Obtener todos los usuarios (solo para admin)
    @GET("/users/")
    suspend fun getAllUsers(): List<UserResponseDTO>

    // Obtener un usuario por ID
    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponseDTO

    // Actualizar un usuario
    @PUT("/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body userUpdateDTO: UserUpdateDTO
    ): UserResponseDTO

    // Eliminar un usuario (solo admin)
    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("/users/isPremium")
    suspend fun isUserPremium(): Response<IsPremiumResponseDTO>

    @PUT("users/setPremium")
    suspend fun setUserPremium(): Response<Boolean>

    // Cambiar el estado de premium de un usuario (solo admin)
    @PUT("/users/changeSubscriptionAdmin/{id}")
    suspend fun toggleUserPremiumByAdmin(@Path("id") userId: String): UserResponseDTO
}