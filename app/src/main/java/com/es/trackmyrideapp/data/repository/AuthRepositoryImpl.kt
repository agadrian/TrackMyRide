package com.es.trackmyrideapp.data.repository

import android.media.session.MediaSession.Token
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
            val idToken = getFirebaseIdToken(email, password)

            // Llamar a la API para iniciar sesión
            val apiResponse = authAPI.login("Bearer $idToken")
            if (!apiResponse.isSuccessful) throw Exception("API login failed")

            val authUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API login response body null")

            // Almacenar tokens
            authPreferences.setJwtToken(authUser.jwtToken)
            authPreferences.setRefreshToken(authUser.refreshToken)

            Result.success(AuthResult(authUser))

            Log.d("FlujoTest", "authrepoimpl setJwtToken y setreefreshtoken llamados. jwt: ${authUser.jwtToken} refresh: ${authUser.refreshToken} ")

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
            Log.d("FlujoTest", "AuthRepositoryImpl. register llamado")
            firebaseAuthService.register(email, password)

            // Inicio sesion en el usuario de firebase para obtener el token real de firebase
            val idToken = getFirebaseIdToken(email, password)

            // Registrar en la API el usuario
            Log.d("FlujoTest", "AuthRepositoryImpl. register. authapi.register llamado")
            val apiResponse = authAPI.register("Bearer $idToken", registrationDTO)
            if (!apiResponse.isSuccessful) {
                Log.d("FlujoTest", "AuthRepositoryImpl. register. apiresponse no es successfull. haciendo rollback en firebase. codigo ${apiResponse.code()}")
                firebaseAuthService.deleteCurrentUser() // Rollback si falla
                throw Exception("API register failed: ${apiResponse.code()}")

            }

            // Obtener usuario autenticado del backend
            val authenticatedUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API register response null")

            Log.d("FlujoTest", "AuthRepositoryImpl. register. authenticated user ${authenticatedUser.uid} name: ${authenticatedUser.username} email: ${authenticatedUser.email}")

            // Registrar tokens
            Log.d("FlujoTest", "AuthRepositoryImpl. register. registrar tokens llamado")
            authPreferences.setJwtToken(authenticatedUser.jwtToken)
            authPreferences.setRefreshToken(authenticatedUser.refreshToken)
            Log.d("FlujoTest", "AuthRepositoryImpl. register. tokens registrados. jwt: ${authenticatedUser.jwtToken} refresh: ${authenticatedUser.refreshToken} ")


            // Devolver AuthResult completo con Firebase + backend user
            Result.success(AuthResult(authenticatedUser))


        } catch (e: Exception) {
            Log.d("FlujoTest", "authrepoimpl register excepcion: ${e.message} ")
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Register)))
        }
    }

    private suspend fun getFirebaseIdToken(email: String, password: String): String {
        Log.d("FlujoTest", "AuthRepositoryImpl. getFirebaseIdToken llamado")
        val firebaseUser = firebaseAuthService.signIn(email, password)
        val idTokenResult = firebaseUser.getIdToken(true).await()

        Log.d("FlujoTest", "AuthRepositoryImpl. getFirebaseIdToken. firebaseUser: ${firebaseUser.uid} idToken: ${idTokenResult.token}")
        return idTokenResult.token ?: throw Exception("Failed to get Firebase ID token")
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


