package com.es.trackmyrideapp.domain.model

data class AuthenticatedUser(
    val jwtToken: String,
    val refreshToken: String
)