package com.es.trackmyrideapp.utils

import android.util.Log
import com.es.trackmyrideapp.data.remote.mappers.Resource
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        val result = apiCall()
        Resource.Success(result)
    } catch (e: HttpException) {
        val errorMessage = try {
            val errorJson = e.response()?.errorBody()?.string()
            val message = JSONObject(errorJson ?: "").optString("message", "HTTP ${e.code()} error")
            message
        } catch (parseError: Exception) {
            "HTTP ${e.code()} error"
        }
        Log.e("safeApiCall", "HttpException: $errorMessage")
        Resource.Error(errorMessage, e.code())
    } catch (e: IOException) {
        val msg = "Network error: check your connection"
        Log.e("safeApiCall", msg, e)
        Resource.Error(msg)
    } catch (e: Exception) {
        val msg = "Unknown error: ${e.localizedMessage ?: "Unexpected error"}"
        Log.e("safeApiCall", msg, e)
        Resource.Error(msg)
    }
}