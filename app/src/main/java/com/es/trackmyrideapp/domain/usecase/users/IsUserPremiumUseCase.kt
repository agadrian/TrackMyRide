package com.es.trackmyrideapp.domain.usecase.users

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.UserRepository
import javax.inject.Inject

class IsUserPremiumUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return userRepository.isUserPremium()
    }
}