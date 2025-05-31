package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.model.FirebaseUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AuthenticatedUser>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    suspend fun register(email: String, password: String, registrationDTO: UserRegistrationDTO): Result<AuthenticatedUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

}