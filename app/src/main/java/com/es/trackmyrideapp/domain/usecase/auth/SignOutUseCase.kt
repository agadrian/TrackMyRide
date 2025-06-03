package com.es.trackmyrideapp.domain.usecase.auth

import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}