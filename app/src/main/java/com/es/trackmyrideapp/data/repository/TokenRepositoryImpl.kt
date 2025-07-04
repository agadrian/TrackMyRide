package com.es.trackmyrideapp.data.repository

import android.util.Log
import com.es.trackmyrideapp.core.states.AuthFlow
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.remote.dto.RefreshTokenRequestDTO
import com.es.trackmyrideapp.data.remote.mappers.ErrorMessageMapper
import com.es.trackmyrideapp.data.remote.mappers.toDomain
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.repository.TokenRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPreferences: AuthPreferences
) : TokenRepository {

    // Mutex para evitar que múltiples peticiones de refreshToken se ejecuten al mismo tiempo
    private val refreshMutex = Mutex()

    override suspend fun refreshToken(): Result<AuthenticatedUser> {
        return refreshMutex.withLock {
            try {
                val refreshToken = authPreferences.getRefreshToken()
                    ?: throw Exception("No refresh token found")

                val response = authApi.refresh(RefreshTokenRequestDTO(refreshToken))
                if (!response.isSuccessful) throw Exception("API refresh failed")

                val authUser = response.body()?.toDomain()
                    ?: throw Exception("API refresh response body null")

                // Guardar nuevos tokens en preferencias
                authPreferences.setJwtToken(authUser.jwtToken)
                authPreferences.setRefreshToken(authUser.refreshToken)

                Result.success(authUser)
            } catch (e: Exception) {
                Log.e("TokenRepo", "Error al refrescar token", e)
                Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Refresh)))
            }
        }
    }

    override suspend fun isJwtTokenValid(): Boolean {
        return try {
            val jwt = authPreferences.getJwtToken() ?: return false
            val response = authApi.validateToken("Bearer $jwt")

            Log.d("TokenRepo", "¿JWT válido? => ${response.isSuccessful}")

            response.isSuccessful
        } catch (e: Exception) {
            Log.d("TokenRepo", "Error al validar JWT: ${e.message}")
            false
        }
    }

    override fun getJwtToken(): String? {
        return authPreferences.getJwtToken()
    }

}

