package com.es.trackmyrideapp.utils

import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.ErrorMessage
import com.google.gson.Gson
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        // Timeout a 10 segundos (ajusta a lo que necesites)
        val result = withTimeout(12_000) {
            apiCall()
        }
        Resource.Success(result)
    } catch (e: TimeoutCancellationException) {
        Resource.Error("Tiempo de espera agotado")
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
        Resource.Error("Error de red: ${e.message ?: "desconocido"}")
    } catch (e: Exception) {
        Resource.Error("Error inesperado: ${e.message ?: "desconocido"}")
    }
}