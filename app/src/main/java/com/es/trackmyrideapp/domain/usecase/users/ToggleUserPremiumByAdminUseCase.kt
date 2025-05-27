package com.es.trackmyrideapp.domain.usecase.users

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.UserRepository
import javax.inject.Inject

class ToggleUserPremiumByAdminUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Resource<User> {
        return userRepository.toggleUserPremiumByAdmin(userId)
    }
}