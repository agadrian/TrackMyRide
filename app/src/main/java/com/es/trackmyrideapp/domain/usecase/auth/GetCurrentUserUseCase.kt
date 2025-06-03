package com.es.trackmyrideapp.domain.usecase.auth

import com.es.trackmyrideapp.domain.model.FirebaseUser
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject


class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): FirebaseUser? = repository.getCurrentUser()
}