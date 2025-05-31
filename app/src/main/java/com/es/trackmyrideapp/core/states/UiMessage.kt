package com.es.trackmyrideapp.core.states

enum class MessageType { INFO, ERROR }

data class UiMessage(
    val message: String,
    val type: MessageType
)
