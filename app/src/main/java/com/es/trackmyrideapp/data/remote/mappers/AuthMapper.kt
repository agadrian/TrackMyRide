package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.AuthResponseDTO
import com.es.trackmyrideapp.domain.model.AuthenticatedUser

fun AuthResponseDTO.toDomain(): AuthenticatedUser {
    return AuthenticatedUser(
        jwtToken = token,
        refreshToken = refreshToken
    )
}