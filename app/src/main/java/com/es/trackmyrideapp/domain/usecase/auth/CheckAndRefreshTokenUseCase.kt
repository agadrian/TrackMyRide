package com.es.trackmyrideapp.domain.usecase.auth


import android.util.Log
import com.es.trackmyrideapp.domain.repository.TokenRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class CheckAndRefreshTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    private val refreshMutex = Mutex()  // Mover el Mutex aquí para proteger toda la operación

    suspend operator fun invoke(): String? {
        return refreshMutex.withLock {  // Proteger toda la operación, no solo el refresh
            withTimeout(12_000) {
                // Verificamos si el token JWT actual es válido
                val isValid = tokenRepository.isJwtTokenValid()

                // Si el token es válido, lo devolvemos directamente
                if (isValid) {
                    Log.d("FlujoTest", "Token válido")
                    return@withTimeout tokenRepository.getJwtToken()
                }

                // Si el token no es válido, intentamos refrescarlo
                val refreshResult = tokenRepository.refreshToken()

                if (refreshResult.isSuccess) {
                    tokenRepository.getJwtToken()
                } else {
                    throw Exception("Refresh token failed")
                }
            }
        }
    }
}