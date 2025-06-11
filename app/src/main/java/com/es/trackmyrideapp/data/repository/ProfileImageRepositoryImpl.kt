package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.ProfileImageApi
import com.es.trackmyrideapp.data.remote.dto.ProfileImageRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.ProfileImage
import com.es.trackmyrideapp.domain.repository.ProfileImageRepository
import com.es.trackmyrideapp.utils.safeApiCall
import javax.inject.Inject

class ProfileImageRepositoryImpl @Inject constructor(
    private val api: ProfileImageApi
) : ProfileImageRepository {

    override suspend fun uploadImage(request: ProfileImageRequestDTO): Resource<ProfileImage> =
        safeApiCall {
            api.uploadImage(request).toDomainModel()
        }

    override suspend fun getImage(): Resource<ProfileImage> =
        safeApiCall {
            api.getImage().toDomainModel()
        }

    override suspend fun deleteImage(): Resource<Unit> =
        safeApiCall {
            api.deleteImage()
        }
}