package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.domain.model.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    //suspend fun getUserRole(): String?
    fun signOut()
    fun getCurrentUser(): User?
    suspend fun register(email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}