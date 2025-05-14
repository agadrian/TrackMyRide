package com.es.trackmyrideapp.domain.usecase.auth

import android.util.Log
import com.es.trackmyrideapp.data.repository.AuthResult
import com.es.trackmyrideapp.domain.repository.AuthRepository
import javax.inject.Inject

class CheckAndRefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        Log.d("CheckAndRefreshTokenUseCase", "Verificando si el JWT es válido...")

        val isValid = authRepository.isJwtTokenValid()
        Log.d("CheckAndRefreshTokenUseCase", "El token JWT es válido: $isValid")

        if (!isValid) {
            Log.d("CheckAndRefreshTokenUseCase", "Token no válido, refrescando con el refresh token...")
            try {
                authRepository.refreshToken()
                Log.d("CheckAndRefreshTokenUseCase", "Token refrescado exitosamente.")
            } catch (e: Exception) {
                Log.e("CheckAndRefreshTokenUseCase", "Error al refrescar el token: ${e.message}")
            }
        } else {
            Log.d("CheckAndRefreshTokenUseCase", "El token ya es válido, no se necesita refrescar.")
        }
    }
}