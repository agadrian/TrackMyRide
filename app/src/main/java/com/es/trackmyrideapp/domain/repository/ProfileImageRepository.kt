package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.ProfileImage

interface ProfileImageRepository {
    suspend fun uploadImage(request: ProfileImageRequestDTO): Resource<ProfileImage>
    suspend fun getImage(): Resource<ProfileImage>
    suspend fun deleteImage(): Resource<Unit>
}