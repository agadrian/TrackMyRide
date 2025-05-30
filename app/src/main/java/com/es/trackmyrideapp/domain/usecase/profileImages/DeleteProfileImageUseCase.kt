package com.es.trackmyrideapp.domain.usecase.profileImages

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.ProfileImageRepository
import javax.inject.Inject


class DeleteProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.deleteImage()
    }
}