package com.es.trackmyrideapp.domain.usecase.images

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.RouteImageRepository
import javax.inject.Inject

class DeleteRouteImageUseCase @Inject constructor(
    private val repository: RouteImageRepository
) {
    suspend operator fun invoke(routeId: Long, imageId: Long): Resource<Unit> {
        return repository.deleteImage(routeId, imageId)
    }
}