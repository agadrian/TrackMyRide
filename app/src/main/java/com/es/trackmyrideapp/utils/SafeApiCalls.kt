package com.es.trackmyrideapp.utils

import com.es.trackmyrideapp.data.remote.mappers.Resource
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Resource<T> {
    return try {
        val result = apiCall()
        Resource.Success(result)
    } catch (e: HttpException) {
        Resource.Error("HTTP ${e.code()} error", e.code())
    } catch (e: IOException) {
        Resource.Error("Network error: check your connection")
    } catch (e: Exception) {
        Resource.Error("Unknown error: ${e.localizedMessage ?: "Unexpected error"}")
    }
}