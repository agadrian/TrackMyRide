package com.es.trackmyrideapp.domain.usecase.routes

import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class CreateRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeCreateDTO: RouteCreateDTO): Resource<Route> {
        return routeRepository.createRoute(routeCreateDTO)
    }
}