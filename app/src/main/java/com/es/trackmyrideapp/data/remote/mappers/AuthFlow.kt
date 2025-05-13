package com.es.trackmyrideapp.data.remote.mappers

sealed class AuthFlow {
    object Login : AuthFlow()
    object Register : AuthFlow()
    object ForgotPassword : AuthFlow()
}