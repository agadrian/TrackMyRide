package com.es.trackmyrideapp.utils

import android.util.Log
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.ErrorMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

/**
 * Ejecuta una llamada a la API de forma segura, manejando excepciones comunes y aplicando timeout.
 *
 * @param apiCall Función suspend que realiza la llamada a la API y devuelve un resultado de tipo T.
 * @return Un objeto Resource que puede ser Success con el resultado o Error con mensaje y código.
 *
 * Mecanismo:
 * - Ejecuta la llamada en un contexto IO para no bloquear el hilo principal.
 * - Aplica un timeout de 12 segundos para evitar esperas indefinidas.
 * - Captura y maneja las excepciones más comunes:
 *    * TimeoutCancellationException: cuando la llamada supera el tiempo límite.
 *    * HttpException: errores HTTP, extrae mensaje personalizado si está disponible.
 *    * IOException: errores de red (sin conexión, etc).
 *    * Exception: cualquier otro error inesperado.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {

        // Timeout a 12 segundos. Llamada con hilos
        val result = withContext(Dispatchers.IO) {
            Log.d("flujotest", "Running on: ${Thread.currentThread().name}")
            withTimeout(12_000) {
                apiCall()
            }
        }
        Resource.Success(result)
    } catch (e: TimeoutCancellationException) {
        Resource.Error("Waitiing time exceeded")
    } catch (e: HttpException) {
        val code = e.code()
        val errorBody = e.response()?.errorBody()?.string()
        val parsedMessage = try {
            errorBody?.let {
                Gson().fromJson(it, ErrorMessage::class.java).message
            } ?: "Error HTTP $code"
        } catch (_: Exception) {
            "Error HTTP $code"
        }
        Resource.Error(message = parsedMessage, code = code)
    } catch (e: IOException) {
        Resource.Error("Network error: ${e.message ?: "Uknown"}")
    } catch (e: Exception) {
        Resource.Error("Uknown error: ${e.message ?: "Uknown"}")
    }
}