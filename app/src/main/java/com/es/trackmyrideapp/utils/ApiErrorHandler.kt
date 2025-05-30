package com.es.trackmyrideapp.utils

import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.data.remote.mappers.Resource

object ApiErrorHandler {

    fun handleApiError(
        resource: Resource.Error,
        customMessages: Map<Int, String> = emptyMap()
    ): UiMessage {
        val message = ApiErrorHandler.getMessage(
            code = resource.code,
            backendMessage = resource.message,
            customMessages = customMessages
        )
        return UiMessage(message ?: "Unexpected error", MessageType.ERROR)
    }

    /**
     * Devuelve un mensaje personalizado según el código de error HTTP.
     *
     * @param code Código HTTP de error, opcional.
     * @param backendMessage El mensaje crudo recibido desde la API.
     * @param customMessages Un mapa opcional para sobrescribir mensajes por código.
     */
    private fun getMessage(
        code: Int?,
        backendMessage: String?,
        customMessages: Map<Int, String> = emptyMap()
    ): String? {
        return when {
            code == null -> "An uknown error occurred"
            customMessages.containsKey(code) -> customMessages[code]
            else -> defaultMessage(code, backendMessage)
        }
    }

    private fun defaultMessage(code: Int, backendMessage: String?): String {
        return when (code) {
            400 -> "The request is invalid. Please check and try again."
            401 -> "You're not authorized. Please log in again."
            403 -> "You don’t have permission to perform this action."
            404 -> "Resource not found."
            409 -> "A conflict occurred with existing data."
            500 -> "Something went wrong. Please try again later."
            else -> backendMessage ?: "Unexpected error occurred."
        }
    }
}
