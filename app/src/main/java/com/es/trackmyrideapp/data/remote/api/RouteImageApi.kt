package com.es.trackmyrideapp.data.remote.api

import com.es.trackmyrideapp.data.remote.dto.RouteImageRequest
import com.es.trackmyrideapp.data.remote.dto.RouteImageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RouteImageApi {

    @POST("/routes/{routeId}/images")
    suspend fun uploadImage(
        @Path("routeId") routeId: Long,
        @Body request: RouteImageRequest
    ): RouteImageResponse

    @GET("/routes/{routeId}/images")
    suspend fun getImages(
        @Path("routeId") routeId: Long
    ): List<RouteImageResponse>

    @DELETE("routes/{routeId}/images/{imageId}")
    suspend fun deleteRouteImage(
        @Path("routeId") routeId: Long,
        @Path("imageId") imageId: Long
    ): Response<Unit>
}