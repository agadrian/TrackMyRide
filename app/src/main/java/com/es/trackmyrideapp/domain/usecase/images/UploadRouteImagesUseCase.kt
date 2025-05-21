package com.es.trackmyrideapp.domain.usecase.images

import com.es.trackmyrideapp.data.remote.dto.RouteImageRequest
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RouteImage
import com.es.trackmyrideapp.domain.repository.RouteImageRepository
import javax.inject.Inject

class UploadRouteImagesUseCase @Inject constructor(
    private val repository: RouteImageRepository
) {
    suspend operator fun invoke(routeId: Long, request: RouteImageRequest): Resource<RouteImage> {
        return repository.uploadImage(routeId, request)
    }
}
