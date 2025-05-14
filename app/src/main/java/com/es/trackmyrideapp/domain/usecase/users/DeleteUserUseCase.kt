package com.es.trackmyrideapp.domain.usecase.users


import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return userRepository.deleteUser(id)
    }
}