package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.VehicleResponseDTO
import com.es.trackmyrideapp.data.remote.dto.VehicleUpdateDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface VehicleApi {

    @POST("/vehicles/init")
    suspend fun createInitialVehicles(): List<VehicleResponseDTO>

    @GET("/vehicles/{type}")
    suspend fun getVehicleByType(@Path("type") type: String): VehicleResponseDTO

    @PUT("/vehicles/{type}")
    suspend fun updateVehicle(
        @Path("type") type: String,
        @Body updateDTO: VehicleUpdateDTO
    ): VehicleResponseDTO
}