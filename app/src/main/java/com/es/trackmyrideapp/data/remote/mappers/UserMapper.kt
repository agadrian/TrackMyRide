package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.UserResponseDTO
import com.es.trackmyrideapp.domain.model.User


fun UserResponseDTO.toDomainModel(): User {
    return User(
        id = this.uid,
        username = this.username,
        email = this.email,
        phone = this.phone,
        isPremium = this.isPremium,
        createdAt = this.createdAt
    )
}