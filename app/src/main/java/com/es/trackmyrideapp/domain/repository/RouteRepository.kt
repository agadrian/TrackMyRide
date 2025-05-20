package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Route


interface RouteRepository {
    suspend fun createRoute(routeCreateDTO: RouteCreateDTO): Resource<Route>
    suspend fun getRouteById(id: Long): Resource<Route>
    suspend fun updateRoute(id: Long, routeUpdateDTO: RouteUpdateDTO): Resource<Route>
    suspend fun deleteRoute(id: Long): Resource<Unit>
    suspend fun getRoutesByUser(): Resource<List<Route>>
}