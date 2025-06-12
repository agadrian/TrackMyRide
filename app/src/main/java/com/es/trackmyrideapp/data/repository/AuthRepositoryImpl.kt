package com.es.trackmyrideapp.data.repository

import android.util.Log
import com.es.trackmyrideapp.core.states.AuthFlow
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.remote.api.AuthApi
import com.es.trackmyrideapp.data.remote.dto.UserRegistrationDTO
import com.es.trackmyrideapp.data.remote.firebase.FirebaseAuthService
import com.es.trackmyrideapp.data.remote.mappers.ErrorMessageMapper
import com.es.trackmyrideapp.data.remote.mappers.toDomain
import com.es.trackmyrideapp.domain.model.AuthenticatedUser
import com.es.trackmyrideapp.domain.model.FirebaseUser
import com.es.trackmyrideapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val authAPI: AuthApi,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthenticatedUser> {
        return try {
            val idToken = getFirebaseIdToken(email, password)

            // Iniciar sesión en la API con el token de Firebase
            val apiResponse = authAPI.login("Bearer $idToken")
            if (!apiResponse.isSuccessful) throw Exception("Sign In Failed. Try again later.")

            val authUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API login response body null")

            // Guardar tokens JWT y Refresh en preferencias locales
            authPreferences.setJwtToken(authUser.jwtToken)
            authPreferences.setRefreshToken(authUser.refreshToken)


            Log.d("AuthRepo", "Tokens guardados: jwt=${authUser.jwtToken}, refresh=${authUser.refreshToken}")

            Result.success(authUser)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Login)))
        }
    }


    /**
     * Registro de nuevo usuario.
     * Si es válido, también se inicia sesión para obtener los tokens automáticamente.
     */
    override suspend fun register(
        email: String,
        password: String,
        registrationDTO: UserRegistrationDTO
    ): Result<AuthenticatedUser> {
        return try {
            // Creo usuario en fireabse
            Log.d("AuthRepo", "Intentando registrar nuevo usuario con Firebase...")
            firebaseAuthService.register(email, password)

            // Obtener token de Firebase iniciando sesión
            val idToken = getFirebaseIdToken(email, password)

            Log.d("AuthRepo", "Registrando usuario en API con el token de Firebase...")

            // Llamada a la API para registrar el usuario en el backend
            val apiResponse = authAPI.register("Bearer $idToken", registrationDTO)
            if (!apiResponse.isSuccessful) {
                Log.d("AuthRepo", "Fallo en la API. Haciendo rollback en Firebase. Código: ${apiResponse.code()}")
                firebaseAuthService.deleteCurrentUser() // Rollback si falla
                throw Exception("Register failed (${apiResponse.code()})")

            }

            // Obtener usuario autenticado del backend
            val authenticatedUser = apiResponse.body()?.toDomain()
                ?: throw Exception("API register response null")

            // Guardar tokens localmente
            authPreferences.setJwtToken(authenticatedUser.jwtToken)
            authPreferences.setRefreshToken(authenticatedUser.refreshToken)

            Log.d("AuthRepo", "Tokens guardados: jwt=${authenticatedUser.jwtToken}, refresh=${authenticatedUser.refreshToken}")

            // Devolver AuthResult con el AuthenticatedUser
            Result.success(authenticatedUser)

        } catch (e: Exception) {
            Log.d("AuthRepo", "Excepción en registro: ${e.message}")
            Result.failure(Exception(ErrorMessageMapper.getMessage(e, AuthFlow.Register)))
        }
    }


    /**
     * Inicia sesión en Firebase para obtener el ID Token del usuario.
     * Este token se utiliza para autenticar contra tu backend.
     */
    private suspend fun getFirebaseIdToken(email: String, password: String): String {
        Log.d("AuthRepo", "Obteniendo ID Token de Firebase...")

        val firebaseUser = firebaseAuthService.signIn(email, password)
        val idTokenResult = firebaseUser.getIdToken(true).await()

        Log.d("AuthRepo", "Firebase UID=${firebaseUser.uid}, token=${idTokenResult.token}")

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

