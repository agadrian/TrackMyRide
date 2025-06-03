package com.es.trackmyrideapp.domain.usecase.routePins

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RoutePin
import com.es.trackmyrideapp.domain.repository.RoutePinRepository
import javax.inject.Inject


class GetPinsByRouteUseCase @Inject constructor(
    private val repository: RoutePinRepository
) {
    suspend operator fun invoke(routeId: Long): Resource<List<RoutePin>> =
        repository.getPinsByRoute(routeId)
}
