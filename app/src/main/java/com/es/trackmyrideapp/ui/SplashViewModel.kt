package com.es.trackmyrideapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val rememberMePreferences: RememberMePreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _shouldNavigateToHome = MutableStateFlow(false)
    val shouldNavigateToHome: StateFlow<Boolean> = _shouldNavigateToHome

    init {
        viewModelScope.launch {
            delay(500) // Para que se vea un poco el splash, opcional
            val shouldAutoLogin = rememberMePreferences.isRememberMe() && getCurrentUserUseCase() != null
            _shouldNavigateToHome.value = shouldAutoLogin
            _isLoading.value = false
        }
    }
}