package com.es.trackmyrideapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.es.trackmyrideapp.data.remote.api.RouteApi
import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.RouteMapper
import com.es.trackmyrideapp.data.remote.mappers.RouteMapper.toDomainModel
import com.es.trackmyrideapp.domain.model.Route
import com.es.trackmyrideapp.domain.repository.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApi: RouteApi
) : RouteRepository {

    override suspend fun createRoute(routeCreateDTO: RouteCreateDTO): Route {
        val routeDTO = routeApi.createRoute(routeCreateDTO)
        return routeDTO.toDomainModel()
    }

    override suspend fun getRouteById(id: Long): Route {
        val routeDTO = routeApi.getRouteById(id)
        return routeDTO.toDomainModel()
    }

    override suspend fun updateRoute(id: Long, routeUpdateDTO: RouteUpdateDTO): Route {
        val routeDTO = routeApi.updateRoute(id, routeUpdateDTO)
        return routeDTO.toDomainModel()
    }

    override suspend fun deleteRoute(id: Long) {
        routeApi.deleteRoute(id)
    }
}