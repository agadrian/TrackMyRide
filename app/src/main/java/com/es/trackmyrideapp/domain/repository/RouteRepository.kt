package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.RouteCreateDTO
import com.es.trackmyrideapp.data.remote.dto.RouteUpdateDTO
import com.es.trackmyrideapp.domain.model.Route


interface RouteRepository {
    suspend fun createRoute(routeCreateDTO: RouteCreateDTO): Route
    suspend fun getRouteById(id: Long): Route
    suspend fun updateRoute(id: Long, routeUpdateDTO: RouteUpdateDTO): Route
    suspend fun deleteRoute(id: Long)
}