package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.RoutePinRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.RoutePin

interface RoutePinRepository {
    suspend fun createPin(pinRequestDTO: RoutePinRequestDTO): Resource<RoutePin>
    suspend fun getPinsByRoute(routeId: Long): Resource<List<RoutePin>>
    suspend fun deletePin(id: Long): Resource<Unit>
}