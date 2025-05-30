package com.es.trackmyrideapp.domain.usecase.routes

import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class UpdateRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(id: Long, routeUpdateDTO: RouteUpdateDTO): Resource<Route> {
        return routeRepository.updateRoute(id, routeUpdateDTO)
    }
}