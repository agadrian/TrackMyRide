package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.ui.viewmodels.ISessionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSessionViewModel : ISessionViewModel {

    private val _isLoading = mutableStateOf(false)
    override val isLoading: State<Boolean> = _isLoading

    private val _uiSnackbar = MutableStateFlow<UiSnackbar?>(null)
    override val uiSnackbar: StateFlow<UiSnackbar?> = _uiSnackbar

    override fun showSnackbar(snackbar: UiSnackbar) {
        _uiSnackbar.value = snackbar
    }

    override fun dismissSnackbar() {
        _uiSnackbar.value = null
    }

    override fun showLoading() {
        _isLoading.value = true
    }

    override fun hideLoading() {
        _isLoading.value = false
    }

    override fun onUserLoggedIn() {
        // vacío para test
    }
}