package com.es.trackmyrideapp.domain.usecase.auth

import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject

class  RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, userData: UserRegistrationDTO): Result<AuthenticatedUser> {
        return repository.register(email, password, userData)
    }
}