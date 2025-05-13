package com.es.trackmyrideapp.data.remote.mappers

import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMessageMapper {
    fun getMessage(exception: Throwable, authFlow: AuthFlow): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> {
                // Correo no encontrado (usuario no registrado)
                "No user found with this email address."
            }

            is FirebaseAuthWeakPasswordException -> {
                // Contraseña débil
                "The password is too weak. Please choose a stronger password."
            }

            is FirebaseAuthInvalidCredentialsException -> {
                when(authFlow){
                    is AuthFlow.ForgotPassword -> "Invalid email, please check"
                    else -> "Invalid credentials. Please check your email and password."
                }
            }

            is FirebaseAuthUserCollisionException -> {
                // Usuario ya registrado con ese correo
                "This email address is already in use."
            }

            is FirebaseAuthRecentLoginRequiredException -> {
                // Se requiere una autenticación reciente
                "This operation requires recent authentication. Please log in again."
            }

            is FirebaseAuthActionCodeException -> {
                // Código de acción inválido o caducado
                "Invalid or expired action code. Please try again."
            }

            is FirebaseAuthEmailException -> {
                // Problema con el formato del correo
                "There was a problem with the email address format."
            }

            is FirebaseAuthException -> {
                // Otros errores generales de FirebaseAuth
                mapFirebaseAuthErrorCode(exception.errorCode)
            }

            is SocketTimeoutException -> {
                // Timeout de conexión
                "Connection timed out. Please check your internet connection."
            }

            is UnknownHostException -> {
                // No hay conexión a internet
                "No internet connection. Please check your network."
            }

            else -> {
                // Mensaje genérico para errores desconocidos
                exception.message ?: "An unexpected error occurred. Please try again."
            }
        }
    }

    private fun mapFirebaseAuthErrorCode(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> {
                // Correo mal formateado
                "The email address is badly formatted."
            }

            "ERROR_USER_DISABLED" -> {
                // Usuario deshabilitado
                "This user account has been disabled."
            }

            "ERROR_USER_NOT_FOUND" -> {
                // Usuario no encontrado (correo no registrado)
                "No user found with this email address."
            }

            "ERROR_WRONG_PASSWORD" -> {
                // Contraseña incorrecta
                "Incorrect password."
            }

            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                // Correo ya está en uso
                "This email is already associated with another account."
            }

            "ERROR_OPERATION_NOT_ALLOWED" -> {
                // Operación no permitida
                "This operation is not allowed. Please contact support."
            }

            "ERROR_WEAK_PASSWORD" -> {
                // Contraseña débil
                "Your password is too weak. Try using a stronger one."
            }

            else -> {
                // Error genérico
                "Authentication failed. Please try again."
            }
        }
    }
}