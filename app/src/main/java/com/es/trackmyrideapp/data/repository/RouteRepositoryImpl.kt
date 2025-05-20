package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.RouteApi
import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import com.es.trackmyrideapp.utils.safeApiCall
import retrofit2.HttpException
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApi: RouteApi
) : RouteRepository {


    override suspend fun createRoute(routeCreateDTO: RouteCreateDTO): Resource<Route> =
        safeApiCall {
            routeApi.createRoute(routeCreateDTO).toDomainModel()
        }

    override suspend fun getRouteById(id: Long): Resource<Route> =
        safeApiCall {
            routeApi.getRouteById(id).toDomainModel()
        }

    override suspend fun getRoutesByUser(): Resource<List<Route>> =
        safeApiCall {
            routeApi.getRoutesByUser().map { it.toDomainModel() }
        }


    override suspend fun updateRoute(id: Long, routeUpdateDTO: RouteUpdateDTO): Resource<Route> =
        safeApiCall {
            routeApi.updateRoute(id, routeUpdateDTO).toDomainModel()
        }

    override suspend fun deleteRoute(id: Long): Resource<Unit> =
        safeApiCall {
            val response = routeApi.deleteRoute(id)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            Unit // Exito
        }
}