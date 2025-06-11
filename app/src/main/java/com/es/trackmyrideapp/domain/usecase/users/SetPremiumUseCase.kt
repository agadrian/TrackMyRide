package com.es.trackmyrideapp.domain.usecase.users

import com.es.trackmyrideapp.data.remote.dto.AuthResponseDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.UserRepository
import javax.inject.Inject

class SetPremiumUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String): Resource<AuthResponseDTO> {
        return userRepository.setUserPremium(token)
    }
}