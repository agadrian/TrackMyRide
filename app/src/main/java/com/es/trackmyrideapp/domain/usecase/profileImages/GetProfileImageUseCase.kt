package com.es.trackmyrideapp.domain.usecase.profileImages

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.ProfileImage
import com.es.trackmyrideapp.domain.repository.ProfileImageRepository
import javax.inject.Inject

class GetProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepository
) {
    suspend operator fun invoke(): Resource<ProfileImage> {
        return repository.getImage()
    }
}