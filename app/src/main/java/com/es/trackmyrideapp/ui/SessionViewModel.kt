package com.es.trackmyrideapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.repository.SessionRepository
import com.es.trackmyrideapp.domain.usecase.GetCurrentUserUseCase
import com.es.trackmyrideapp.domain.usecase.SignOutUseCase
import com.es.trackmyrideapp.domain.usecase.auth.CheckAndRefreshTokenUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.CreateInitialVehiclesUseCase
import com.es.trackmyrideapp.ui.components.VehicleType
import com.google.maps.android.compose.MapType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authPreferences: AuthPreferences,
    private val checkAndRefreshTokenUseCase: CheckAndRefreshTokenUseCase,
    private val sessionRepository: SessionRepository,
    private val createInitialVehiclesUseCase: CreateInitialVehiclesUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    init {
        checkAuthState()
        _userRole.value = authPreferences.getUserRoleFromToken()
    }

    // Borrar tokens de encryptedsharedpreferences, el rememberMe y hacer singout
    fun logout() {
        Log.d("FlujoTest", "sesionviewmodel -> logout llamado...")
        rememberMePreferences.clearRememberMe()
        authPreferences.clearAllTokens()
        signOutUseCase()
        Log.d("FlujoTest", "Token shared pref: ${authPreferences.getJwtToken()}. Refreshtoken prefs ${authPreferences.getRefreshToken()}")
        _userRole.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            Log.d("SessionViewModel", "Verificando el estado de la autenticación...")
            Log.d("FlujoTest", "- Verificando el estado de la autenticación...")
            val shouldAutoLogin = rememberMePreferences.isRememberMe() && getCurrentUserUseCase() != null
            Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin: $shouldAutoLogin")


            if (shouldAutoLogin) {
                try {
                    Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin...")
                    Log.d("FlujoTest", "Intentando refrescar token si es necesario...")
                    // Comprobar que el jwt guardado sea valido, y renovarlo en cado de que no
                    withTimeout(5000) {
                        Log.d("FlujoTest", "Dentro de timeout5000 checkAndRefreshTokenUseCase...")
                        val newToken = checkAndRefreshTokenUseCase()

                        // Obtener el rol del usuario para comprobar a que pantalla llevarlo
                        if (newToken != null) {
                            _userRole.value = authPreferences.getUserRoleFromToken()
                        }else{
                            _userRole.value = null
                        }
                        Log.d("FlujoTest", "checkAndRefreshTokenUseCase completado correctamente con: $newToken")
                    }


                    Log.d("FlujoTest", "Saliendo del timeout de 5000 para el chekAndRefreshTokenUseCase...")
                    _authState.value = AuthState.Authenticated
                    loadInitialVehicles()
                } catch (e: Throwable) {
                    Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin.. catch. Se llama al logout Exception: ${e.message}")
                    logout()
                    _authState.value = AuthState.Unauthenticated
                }
            } else {
                Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin -> false...")
                _userRole.value = null
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
    val selectedVehicle = sessionRepository.selectedVehicle
    fun selectVehicle(vehicle: VehicleType) {
        sessionRepository.setSelectedVehicle(vehicle)
    }

    private suspend fun loadInitialVehicles() {
        when (val result = createInitialVehiclesUseCase()) {
            is Resource.Success -> {
                // Verificar que los vehículos se crearon correctamente
                if (result.data.isNotEmpty()) {
                    sessionRepository.setSelectedVehicle(VehicleType.CAR)

                } else {
                    Log.e("SessionViewModel", "Los vehículos ya estaban creados")
                }
            }
            is Resource.Error -> {
                Log.e("SessionViewModel", "Error creando vehículos: ${result.message}")
            }
            Resource.Loading -> {
               // Nada
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}
