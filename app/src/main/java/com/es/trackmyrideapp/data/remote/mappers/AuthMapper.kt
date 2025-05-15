package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.AuthResponseDTO
import com.es.trackmyrideapp.domain.model.AuthenticatedUser

fun AuthResponseDTO.toDomain(): AuthenticatedUser {
    return AuthenticatedUser(
        uid = uid,
        email = email,
        username = username,
        jwtToken = token,
        refreshToken = refreshToken
    )
}