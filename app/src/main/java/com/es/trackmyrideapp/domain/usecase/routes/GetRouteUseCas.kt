package com.es.trackmyrideapp.domain.usecase.routes

import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class CreateRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {

    suspend fun execute(routeCreateDTO: RouteCreateDTO): Route {
        return routeRepository.createRoute(routeCreateDTO)
    }
}