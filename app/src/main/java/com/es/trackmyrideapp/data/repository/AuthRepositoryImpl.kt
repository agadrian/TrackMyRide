package com.es.trackmyrideapp.data.repository

import android.util.Log
import com.es.trackmyrideapp.data.remote.firebase.FirebaseAuthService
import com.es.trackmyrideapp.data.remote.mappers.AuthFlow
import com.es.trackmyrideapp.data.remote.mappers.ErrorMessageMapper
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val firebaseUser = firebaseAuthService.signIn(email, password)
            val idTokenResult = firebaseUser.getIdToken(true).await()
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                idToken = idTokenResult.token
            )
            Log.d("Firebase", "User legged successfully: $user. Token: ${user.idToken}")
            Log.d("Firebase", "Token: ${user.idToken}")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Login)))
        }
    }


    /**
     * Registro anuevo usuario. Si es valido, hacerle Log In automaticamente para el token.
     */
    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            firebaseAuthService.register(email, password)
            val firebaseUserResult = signIn(email, password)


            if (firebaseUserResult.isSuccess) {
                val firebaseUser = firebaseUserResult.getOrNull()

                if (firebaseUser != null) {
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        idToken = firebaseUser.idToken
                    )

                    Result.success(user)
                } else {
                    Result.failure(Exception("Firebase user is null"))
                }
            } else {
                Result.failure(Exception("Login failed during registration"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Register)))
        }
    }


    override fun signOut() {
        firebaseAuthService.signOut()
    }

    override fun getCurrentUser(): User? {
        return firebaseAuthService.getCurrentUser()?.let {
            User(it.uid, it.email)
        }
    }


    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuthService.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.ForgotPassword)))
        }
    }}