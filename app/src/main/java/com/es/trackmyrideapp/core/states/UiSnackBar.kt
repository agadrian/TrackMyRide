package com.es.trackmyrideapp.core.states

data class UiSnackbar(
    val message: String,
    val messageType: MessageType,
    val actionLabel: String? = null,
    val withDismissAction: Boolean = false
)