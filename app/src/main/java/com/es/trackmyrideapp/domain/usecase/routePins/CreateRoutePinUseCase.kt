package com.es.trackmyrideapp.domain.usecase.routePins

import com.es.trackmyrideapp.data.remote.dto.RoutePinRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RoutePin
import com.es.trackmyrideapp.domain.repository.RoutePinRepository
import javax.inject.Inject

class CreateRoutePinUseCase @Inject constructor(
    private val repository: RoutePinRepository
) {
    suspend operator fun invoke(pinRequestDTO: RoutePinRequestDTO): Resource<RoutePin> =
        repository.createPin(pinRequestDTO)
}