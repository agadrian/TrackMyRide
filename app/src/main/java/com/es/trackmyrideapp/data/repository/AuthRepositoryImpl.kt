package com.es.trackmyrideapp.data.repository

import android.util.Log
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.remote.api.RefreshTokenRequest
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.data.remote.firebase.FirebaseAuthService
import com.es.trackmyrideapp.data.remote.mappers.AuthFlow
import com.es.trackmyrideapp.data.remote.mappers.ErrorMessageMapper
import com.es.trackmyrideapp.data.remote.mappers.toDomain
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.model.FirebaseUser
import com.es.trackmyrideapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val authAPI: AuthApi,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthResult> {
        return try {
            val firebaseUser = firebaseAuthService.signIn(email, password)
            val idTokenResult = firebaseUser.getIdToken(true).await()

            // TODO: QUITAR
            val user = FirebaseUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                idToken = idTokenResult.token
            )

            val apiResponse = authAPI.login("Bearer ${idTokenResult.token}")
            Log.d("Firebase", "token pasado a la api:  ${idTokenResult.token}")
            Log.d("FlujoTest", "authrepoimpl: token pasado  a la api ${idTokenResult.token}")
            if (!apiResponse.isSuccessful) throw Exception("API login failed")

            Log.d("FlujoTest", "Si veo esto, api.LOGIN CORRECTO")

            // Uusario autenticado del backend
            val authUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API login response body null")

            // Almacenar JWT y refresh token
            authPreferences.setJwtToken(authUser.jwtToken)
            authPreferences.setRefreshToken(authUser.refreshToken)

            Log.d("FlujoTest", "authrepoimpl setJwtToken y setreefreshtoken llamados. jwt: ${authUser.jwtToken} refresh: ${authUser.refreshToken} ")


            Log.d("Firebase", "User legged successfully: $user. Token: ${user.idToken}")
            Log.d("Firebase", "Token: ${user.idToken}")

            Log.d("FlujoTest", "User logged successfully: $user. Token: ${user.idToken}")
            Result.success(AuthResult(authUser))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Login)))
        }
    }


    /**
     * Registro a nuevo usuario. Si es valido, hacerle Log In automaticamente para el token.
     */
    override suspend fun register(
        email: String,
        password: String,
        registrationDTO: UserRegistrationDTO
    ): Result<AuthResult> {
        return try {
            // Creo usuario en fireabse
            firebaseAuthService.register(email, password)

            Log.d("FlujoTest", "authrepoimpl firebase.register llamado ")

            // Lo logeo para obetener el token de firebase
            val firebaseAuthResult = signIn(email, password)

            Log.d("FlujoTest", "authrepoimpl llamada a sigin dentro de register: ${firebaseAuthResult.isSuccess} ")


            if (firebaseAuthResult.isSuccess) {
                val authResult = firebaseAuthResult.getOrThrow()
                Log.d("FlujoTest", "authrepoimpl register. authresult uid: ${authResult.apiUser.uid} authresult email: ${authResult.apiUser.email} ")

                // Token jwt
                val idToken = authResult.apiUser.jwtToken

                Log.d("FlujoTest", "authrepoimpl register. idToken: $idToken ")

                // Registrarlo en la api
                val apiResponse = authAPI.register("Bearer $idToken", registrationDTO)

                // Si el registro en la api dalla, borrar el usuario en firebase automaticamente
                if (!apiResponse.isSuccessful) {
                    firebaseAuthService.deleteCurrentUser()
                    throw Exception("API register failed: ${apiResponse.code()}")
                }


                // Obtener el usuario autenticado del backend
                val authenticatedUser = apiResponse.body()?.toDomain()
                    ?: throw Exception("API register response null")

                // Devolver AuthResult completo con Firebase + backend user
                Result.success(AuthResult(authenticatedUser))

            } else {
                Log.d("FlujoTest", "authrepoimpl login failed after register ")
                Result.failure(Exception("Login failed after register"))
            }
        } catch (e: Exception) {
            Log.d("FlujoTest", "authrepoimpl register excepcion: ${e.message} ")
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Register)))
        }
    }


    override suspend fun refreshToken(): Result<AuthResult> {
        return try {
            val refreshToken = authPreferences.getRefreshToken() ?: throw Exception("No refresh token found")

            val response = authAPI.refresh(RefreshTokenRequest(refreshToken))

            if (!response.isSuccessful) throw Exception("API refresh failed")

            val authUser = response.body()?.toDomain()
                ?: throw Exception("API refresh response body null")

            // Guardar los nuevos tokens
            authPreferences.setJwtToken(authUser.jwtToken)
            authPreferences.setRefreshToken(authUser.refreshToken)

            Result.success(AuthResult(authUser))
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Refresh)))
        }
    }

    override suspend fun isJwtTokenValid(): Boolean {
        return try {
            val jwt = authPreferences.getJwtToken() ?: return false
            val response = authAPI.validateToken("Bearer $jwt")
            response.isSuccessful
        } catch (e: Exception) {
            false
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
    //val firebaseUser: FirebaseUser,
    val apiUser: AuthenticatedUser
)

