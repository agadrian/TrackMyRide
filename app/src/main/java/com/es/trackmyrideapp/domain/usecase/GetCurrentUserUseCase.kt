package com.es.trackmyrideapp.domain.usecase

import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject


class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): User? = repository.getCurrentUser()
}