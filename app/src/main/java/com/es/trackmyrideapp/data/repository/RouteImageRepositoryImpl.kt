package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.RouteImageApi
import com.es.trackmyrideapp.data.remote.dto.RouteImageRequest
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.RouteImage
import com.es.trackmyrideapp.domain.repository.RouteImageRepository
import com.es.trackmyrideapp.utils.safeApiCall
import javax.inject.Inject

class RouteImageRepositoryImpl @Inject constructor(
    private val routeImageApi: RouteImageApi
) : RouteImageRepository {

    override suspend fun uploadImage(routeId: Long, request: RouteImageRequest): Resource<RouteImage> =
        safeApiCall {
            routeImageApi.uploadImage(routeId, request).toDomainModel()
        }

    override suspend fun getImages(routeId: Long): Resource<List<RouteImage>> =
        safeApiCall {
            routeImageApi.getImages(routeId).map { it.toDomainModel() }
        }

    override suspend fun deleteImage(routeId: Long, imageId: Long): Resource<Unit> =
        safeApiCall {
            routeImageApi.deleteRouteImage(routeId, imageId) // Unit si OK
        }
}