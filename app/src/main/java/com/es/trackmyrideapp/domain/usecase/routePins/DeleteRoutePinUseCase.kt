package com.es.trackmyrideapp.domain.usecase.routePins

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.RoutePinRepository
import javax.inject.Inject

class DeleteRoutePinUseCase @Inject constructor(
    private val repository: RoutePinRepository
) {
    suspend operator fun invoke(pinId: Long): Resource<Unit> =
        repository.deletePin(pinId)
}