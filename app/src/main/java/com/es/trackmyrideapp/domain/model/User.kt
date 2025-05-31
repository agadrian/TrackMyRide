package com.es.trackmyrideapp.domain.model

import java.util.Date

data class User(
    val id: String,
    val username: String,
    val email: String,
    val phone: String?,
    val photoUrl: String? = null,
    val isPremium: Boolean,
    val createdAt: Date
)