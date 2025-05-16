package com.es.trackmyrideapp.domain.usecase.auth

import android.util.Log
import com.es.trackmyrideapp.data.repository.AuthResult
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject

class CheckAndRefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        val isValid = authRepository.isJwtTokenValid()
        if (!isValid) {
            val refreshResult = authRepository.refreshToken()
            if (refreshResult.isFailure) {
                throw Exception("Refresh token failed")
            }
        }
    }
}