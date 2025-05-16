package com.es.trackmyrideapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.GetCurrentUserUseCase
import com.es.trackmyrideapp.domain.usecase.SignOutUseCase
import com.es.trackmyrideapp.domain.usecase.auth.CheckAndRefreshTokenUseCase
import com.es.trackmyrideapp.ui.components.VehicleType
import com.google.maps.android.compose.MapType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authPreferences: AuthPreferences,
    private val checkAndRefreshTokenUseCase: CheckAndRefreshTokenUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun logout() {
        rememberMePreferences.clearRememberMe()
        authPreferences.clearJwtToken()
        signOutUseCase()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            Log.d("SessionViewModel", "Verificando el estado de la autenticación...")
            Log.d("FlujoTest", "Verificando el estado de la autenticación...")
            val shouldAutoLogin = rememberMePreferences.isRememberMe() && getCurrentUserUseCase() != null


            if (shouldAutoLogin) {
                try {
                    Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin...")
                    Log.d("SessionViewModel", "Intentando refrescar token si es necesario...")
                    // Comprobar que el jwt guardado sea valido, y renovarlo en cado de que no
                    checkAndRefreshTokenUseCase()
                    _authState.value = AuthState.Authenticated
                } catch (e: Exception) {
                    logout() // TODO: TEST
                    _authState.value = AuthState.Unauthenticated
                }
            } else {
                Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin -> false...")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /*
    Meto aqui los estados de la barra superior ya que este es u viewmodel generico que se peude usar en cualquier pantalla, por lo que me va perfecto para acceder a estos estados y modificarlos desde culauqier lugar
     */

    // Estado tipo mapa
    private val _mapType = MutableStateFlow(MapType.NORMAL)
    val mapType: StateFlow<MapType> = _mapType.asStateFlow()

    fun setMapType(type: MapType) {
        _mapType.value = type
    }


    // Estado tipo vehiculo
    private val _vehicleType = MutableStateFlow(VehicleType.CAR)
    val vehicleType: StateFlow<VehicleType> = _vehicleType.asStateFlow()

    fun setVehicleType(type: VehicleType) {
        _vehicleType.value = type
        Log.d("SessionViewModel", "Cambiado tipo de vehiculo a: $type")
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}