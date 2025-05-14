package com.es.trackmyrideapp.data.remote.dto

data class AuthResponseDTO(
    val token: String,
    val uid: String,
    val email: String,
    val role: String,
    val username: String
)