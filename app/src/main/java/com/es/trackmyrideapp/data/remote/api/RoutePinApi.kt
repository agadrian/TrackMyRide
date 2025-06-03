package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.RoutePinRequestDTO
import com.es.trackmyrideapp.data.remote.dto.RoutePinResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoutePinApi {

    // Crear un pin para una ruta
    @POST("/route-pins/")
    suspend fun createPin(@Body pinRequestDTO: RoutePinRequestDTO): RoutePinResponseDTO

    // Obtener todos los pins de una ruta
    @GET("/route-pins/route/{routeId}")
    suspend fun getPinsByRoute(@Path("routeId") routeId: Long): List<RoutePinResponseDTO>

    // Borrar un pin por id
    @DELETE("/route-pins/{id}")
    suspend fun deletePin(@Path("id") id: Long): Response<Unit>
}