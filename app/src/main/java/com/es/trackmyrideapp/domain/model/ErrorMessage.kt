package com.es.trackmyrideapp.domain.model


data class ErrorMessage(
    val status: Int,
    val message: String,
    val path: String
)