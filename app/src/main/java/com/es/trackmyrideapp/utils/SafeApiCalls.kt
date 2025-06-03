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