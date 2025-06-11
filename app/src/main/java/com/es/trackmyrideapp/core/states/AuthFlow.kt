package com.es.trackmyrideapp.core.states

sealed class AuthFlow {
    object Login : AuthFlow()
    object Register : AuthFlow()
    object Refresh : AuthFlow()
    object ForgotPassword : AuthFlow()
}