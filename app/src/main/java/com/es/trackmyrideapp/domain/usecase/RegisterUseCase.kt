package com.es.trackmyrideapp.domain.usecase

import com.es.trackmyrideapp.data.repository.AuthResult
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResult> {
        return repository.register(email, password)
    }
}