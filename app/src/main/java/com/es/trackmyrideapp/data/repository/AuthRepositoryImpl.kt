package com.es.trackmyrideapp.data.repository

import android.util.Log
import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.data.remote.firebase.FirebaseAuthService
import com.es.trackmyrideapp.data.remote.mappers.AuthFlow
import com.es.trackmyrideapp.data.remote.mappers.ErrorMessageMapper
import com.es.trackmyrideapp.data.remote.mappers.toDomain
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.model.FirebaseUser
import com.es.trackmyrideapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val authAPI: AuthApi
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthResult> {
        return try {
            val firebaseUser = firebaseAuthService.signIn(email, password)
            val idTokenResult = firebaseUser.getIdToken(true).await()
            val user = FirebaseUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                idToken = idTokenResult.token
            )

            val apiResponse = authAPI.login("Bearer $idTokenResult")
            if (!apiResponse.isSuccessful) throw Exception("API login failed")

            val authUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API login response body null")


            Log.d("Firebase", "User legged successfully: $user. Token: ${user.idToken}")
            Log.d("Firebase", "Token: ${user.idToken}")
            Result.success(AuthResult(user, authUser))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Login)))
        }
    }


    /**
     * Registro anuevo usuario. Si es valido, hacerle Log In automaticamente para el token.
     */
    override suspend fun register(email: String, password: String): Result<AuthResult> {
        return try {
            // Creo usuario en fireabse
            firebaseAuthService.register(email, password)
            // Lo logeo para obetener el token de firebase
            val firebaseAuthResult = signIn(email, password)


            if (firebaseAuthResult.isSuccess) {
                val authResult = firebaseAuthResult.getOrThrow()
                val firebaseUser = authResult.firebaseUser
                val idToken = firebaseUser.idToken ?: throw Exception("No Firebase token")


                val registrationDTO = UserRegistrationDTO(
                    username = "defaultUsername", // <-- pásarlo desde la pantalla de registro
                    phone = "000000000" // <-- pásalo desde la pantalla de registro
                )

                val apiResponse = authAPI.register("Bearer $idToken", registrationDTO)

                if (!apiResponse.isSuccessful) throw Exception("API register failed: ${apiResponse.code()}")

                val authenticatedUser = apiResponse.body()?.toDomain()
                    ?: throw Exception("API register response null")

                // Devolver AuthResult completo con Firebase + backend user
                Result.success(AuthResult(firebaseUser, authenticatedUser))

//                if (firebaseUser != null) {
//                    val user = User(
//                        uid = firebaseUser.uid,
//                        email = firebaseUser.email,
//                        idToken = firebaseUser.idToken
//                    )
//
//                    Result.success(user)
            } else {
                Result.failure(Exception("Login failed after register"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Register)))
        }
    }


    override fun signOut() {
        firebaseAuthService.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuthService.getCurrentUser()?.let {
            FirebaseUser(it.uid, it.email)
        }
    }


    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuthService.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.ForgotPassword)))
        }
    }
}

data class AuthResult(
    val firebaseUser: FirebaseUser,
    val apiUser: AuthenticatedUser
)