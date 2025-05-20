package com.es.trackmyrideapp.core.states

enum class MessageType { INFO, ERROR }

data class UiMessage(
    val message: String,
    val type: MessageType
)


/*
private val _uiMessage = MutableStateFlow<UiMessage?>(null)
val uiMessage: StateFlow<UiMessage?> = _uiMessage

fun consumeUiMessage() {
    _uiMessage.value = null
}
 */