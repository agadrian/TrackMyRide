package com.es.trackmyrideapp.domain.usecase.routes

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class DeleteRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: Long): Resource<Unit> {
        return routeRepository.deleteRoute(id)
    }
}