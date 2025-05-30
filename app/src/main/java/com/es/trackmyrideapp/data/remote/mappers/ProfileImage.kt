package com.es.trackmyrideapp.data.remote.mappers

import com.es.trackmyrideapp.data.remote.dto.ProfileImageResponse
import com.es.trackmyrideapp.domain.model.ProfileImage

fun ProfileImageResponse.toDomainModel(): ProfileImage {
    return ProfileImage(
        id = this.id,
        imageUrl = this.imageUrl,
        uploadedAt = this.uploadedAt
    )
}