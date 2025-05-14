package com.es.trackmyrideapp.domain.usecase.routes

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class GetRouteByIdUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: Long): Resource<Route> {
        return routeRepository.getRouteById(id)
    }
}