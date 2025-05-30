package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequest
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.ProfileImage

interface ProfileImageRepository {
    suspend fun uploadImage(request: ProfileImageRequest): Resource<ProfileImage>
    suspend fun getImage(): Resource<ProfileImage>
    suspend fun deleteImage(): Resource<Unit>
}