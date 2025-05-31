package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.domain.model.AuthenticatedUser

interface TokenRepository {
    suspend fun refreshToken(): Result<AuthenticatedUser>
    suspend fun isJwtTokenValid(): Boolean
    fun getJwtToken(): String?
}