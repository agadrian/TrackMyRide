package com.es.trackmyrideapp.data.remote.dto

// Se envia a la API para actualizar user
data class UserUpdateDTO(
    val username: String? = null,
    val phone: String? = null
    // val photoUrl: String? = null // futuro
)