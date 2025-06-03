package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.RoutePinApi
import com.es.trackmyrideapp.data.remote.dto.RoutePinRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.RoutePin
import com.es.trackmyrideapp.domain.repository.RoutePinRepository
import com.es.trackmyrideapp.utils.safeApiCall
import retrofit2.HttpException
import javax.inject.Inject

class RoutePinRepositoryImpl @Inject constructor(
    private val routePinApi: RoutePinApi
) : RoutePinRepository {

    override suspend fun createPin(pinRequestDTO: RoutePinRequestDTO): Resource<RoutePin> =
        safeApiCall {
            routePinApi.createPin(pinRequestDTO).toDomainModel()
        }

    override suspend fun getPinsByRoute(routeId: Long): Resource<List<RoutePin>> =
        safeApiCall {
            routePinApi.getPinsByRoute(routeId).map { it.toDomainModel() }
        }

    override suspend fun deletePin(id: Long): Resource<Unit> =
        safeApiCall {
            val response = routePinApi.deletePin(id)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            Unit
        }
}