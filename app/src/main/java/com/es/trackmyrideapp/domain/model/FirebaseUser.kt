package com.es.trackmyrideapp.domain.model

data class FirebaseUser(
    val uid: String,
    val email: String?,
    val idToken: String? = null
)
