package com.es.trackmyrideapp.data.remote.dto

import java.util.Date

// Recibir los datos de la API
data class UserResponseDTO(
    val uid: String,
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
    val isPremium: Boolean,
    val createdAt: Date
)

