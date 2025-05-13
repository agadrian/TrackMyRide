package com.es.trackmyrideapp.domain.model

data class User(
    val uid: String,
    val email: String?,
    val idToken: String? = null
)
