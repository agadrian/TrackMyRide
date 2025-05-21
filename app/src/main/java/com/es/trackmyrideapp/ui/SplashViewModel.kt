package com.es.trackmyrideapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val authPreferences: AuthPreferences,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _shouldNavigateTo = MutableStateFlow<String?>(null)
    val shouldNavigateTo: StateFlow<String?> = _shouldNavigateTo

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    init {
        viewModelScope.launch {
            Log.d("Flujotest", "INIT DEL SESSIONVIEWMODEL")
            delay(500) // Para que se vea un poco el splash, opcional
            val user = getCurrentUserUseCase()

            val shouldAutoLogin = rememberMePreferences.isRememberMe() && user != null

            if (shouldAutoLogin){
                val role = authPreferences.getUserRoleFromToken()
                _shouldNavigateTo.value = when (role) {
                    "ADMIN" -> "admin"
                    else -> "home"
                }
            }else{
                _shouldNavigateTo.value = "login"
            }
            //_shouldNavigateToHome.value = shouldAutoLogin
            _isLoading.value = false
        }
    }
}