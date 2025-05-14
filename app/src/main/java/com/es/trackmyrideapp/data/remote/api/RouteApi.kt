package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteResponseDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RouteApi {

    // Crear una nueva ruta
    @POST("/routes/")
    suspend fun createRoute(@Body routeCreateDTO: RouteCreateDTO): RouteResponseDTO

    // Obtener una ruta por su ID
    @GET("/routes/{id}")
    suspend fun getRouteById(@Path("id") id: Long): RouteResponseDTO

    @GET("/routes/user/{userId}")
    suspend fun getRoutesByUser(@Path("userId") userId: String): List<RouteResponseDTO>

    // Actualizar una ruta existente
    @PUT("/routes/{id}")
    suspend fun updateRoute(
        @Path("id") id: Long,
        @Body routeUpdateDTO: RouteUpdateDTO
    ): RouteResponseDTO

    // Eliminar una ruta por su ID
    @DELETE("/routes/{id}")
    suspend fun deleteRoute(@Path("id") id: Long)
}