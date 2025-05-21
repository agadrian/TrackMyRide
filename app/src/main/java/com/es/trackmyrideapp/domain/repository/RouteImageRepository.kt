package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.RouteImageRequest
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RouteImage

interface RouteImageRepository {
    suspend fun uploadImage(routeId: Long, request: RouteImageRequest): Resource<RouteImage>
    suspend fun getImages(routeId: Long): Resource<List<RouteImage>>
    suspend fun deleteImage(routeId: Long, imageId: Long): Resource<Unit>
}