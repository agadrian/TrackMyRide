package com.es.trackmyrideapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.es.trackmyrideapp.data.remote.api.RouteApi
import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import com.es.trackmyrideapp.utils.safeApiCall
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApi: RouteApi
) : RouteRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createRoute(routeCreateDTO: RouteCreateDTO): Resource<Route> =
        safeApiCall {
            routeApi.createRoute(routeCreateDTO).toDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRouteById(id: Long): Resource<Route> =
        safeApiCall {
            routeApi.getRouteById(id).toDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRoutesByUser(userId: String): Resource<List<Route>> =
        safeApiCall {
            routeApi.getRoutesByUser(userId).map { it.toDomainModel() }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateRoute(id: Long, routeUpdateDTO: RouteUpdateDTO): Resource<Route> =
        safeApiCall {
            routeApi.updateRoute(id, routeUpdateDTO).toDomainModel()
        }

    override suspend fun deleteRoute(id: Long): Resource<Unit> =
        safeApiCall {
            routeApi.deleteRoute(id)
        }
}