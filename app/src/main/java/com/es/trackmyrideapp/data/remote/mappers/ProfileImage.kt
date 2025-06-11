package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.ProfileImageResponseDTO
import com.es.trackmyrideapp.domain.model.ProfileImage

fun ProfileImageResponseDTO.toDomainModel(): ProfileImage {
    return ProfileImage(
        id = this.id,
        imageUrl = this.imageUrl,
        uploadedAt = this.uploadedAt
    )
}