package com.es.trackmyrideapp.domain.usecase.auth


import android.util.Log
import com.es.trackmyrideapp.data.repository.TokenRepository
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class CheckAndRefreshTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(): String? {
        return withTimeout(10_000) {

            // Verificamos si el token JWT actual es válido
            val isValid = tokenRepository.isJwtTokenValid()

            // Si el token es válido, lo devolvemos directamente
            if (isValid) {
                Log.d("FlujoTest", "checkAndRefreshTokenUseCase -> token es valido. token jwt: ${tokenRepository.getJwtToken()}")
                return@withTimeout tokenRepository.getJwtToken() // Retorna el token actual
            }

            // Si el token no es válido, intentamos refrescarlo
            val refreshResult = tokenRepository.refreshToken()

            // Si el refresco fue exitoso, devolvemos el nuevo token JWT
            return@withTimeout if (refreshResult.isSuccess) {
                tokenRepository.getJwtToken() // Retorna el nuevo JWT guardado
            } else {
                throw Exception("Refresh token failed")
            }
        }
    }
}