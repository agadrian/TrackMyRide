package com.es.trackmyrideapp.domain.model

data class AuthenticatedUser(
    val uid: String,
    val email: String,
    val username: String,
    val jwtToken: String,
    val refreshToken: String
)