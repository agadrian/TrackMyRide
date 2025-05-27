package com.es.trackmyrideapp.data.remote

import com.es.trackmyrideapp.data.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRefreshRepo: dagger.Lazy<TokenRepository>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Ya intentó autenticarse con este token
        if (responseCount(response) >= 2) return null

        val newToken = try {
            runBlocking {
                val result = tokenRefreshRepo.get().refreshToken()
                if (result.isSuccess) result.getOrNull()?.authenticatedUser?.jwtToken else null
            }
        } catch (e: Exception) {
            null
        }

        return if (!newToken.isNullOrBlank()) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}