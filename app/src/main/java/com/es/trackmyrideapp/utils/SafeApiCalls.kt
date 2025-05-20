package com.es.trackmyrideapp.utils

import com.es.trackmyrideapp.data.remote.mappers.Resource
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: HttpException) {
        val errorMessage = e.response()?.errorBody()?.use {
            it.string().takeIf { str -> str.isNotBlank() } ?: "HTTP ${e.code()}"
        } ?: "HTTP ${e.code()}"
        Resource.Error(errorMessage, e.code())
    } catch (e: IOException) {
        Resource.Error("Network error: ${e.message ?: "Unknown"}")
    } catch (e: Exception) {
        Resource.Error("Unexpected error: ${e.message ?: "Unknown"}")
    }
}