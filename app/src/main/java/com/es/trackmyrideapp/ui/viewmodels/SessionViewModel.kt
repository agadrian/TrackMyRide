package com.es.trackmyrideapp.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.AuthState
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.repository.SessionRepository
import com.es.trackmyrideapp.domain.usecase.auth.CheckAndRefreshTokenUseCase
import com.es.trackmyrideapp.domain.usecase.auth.GetCurrentUserUseCase
import com.es.trackmyrideapp.domain.usecase.auth.SignOutUseCase
import com.es.trackmyrideapp.domain.usecase.profileImages.GetProfileImageUseCase
import com.es.trackmyrideapp.domain.usecase.users.GetUserByIdUseCase
import com.es.trackmyrideapp.domain.usecase.users.IsUserPremiumUseCase
import com.es.trackmyrideapp.domain.usecase.users.SetPremiumUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.CreateInitialVehiclesUseCase
import com.es.trackmyrideapp.ui.components.VehicleType
import com.google.maps.android.compose.MapType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


interface ISessionViewModel {
    fun showLoading()
    fun hideLoading()
    fun onUserLoggedIn()
    val isLoading: State<Boolean>
    val uiSnackbar: StateFlow<UiSnackbar?>
    fun showSnackbar(snackbar: UiSnackbar)
    fun dismissSnackbar()
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authPreferences: AuthPreferences,
    private val checkAndRefreshTokenUseCase: CheckAndRefreshTokenUseCase,
    private val sessionRepository: SessionRepository,
    private val createInitialVehiclesUseCase: CreateInitialVehiclesUseCase,
    private val isUserPremiumUseCase: IsUserPremiumUseCase,
    private val setPremiumUseCase: SetPremiumUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getProfileImageUseCase: GetProfileImageUseCase
) : ViewModel(), ISessionViewModel {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    private val _isPremium = MutableStateFlow<Boolean>(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _userName = MutableStateFlow<String>("TrackMyRide User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    override val isLoading: State<Boolean> = _isLoading

    private val _premiumActivated = MutableStateFlow(false)
    val premiumActivated: StateFlow<Boolean> = _premiumActivated

    fun resetPremiumActivationFlag() {
        _premiumActivated.value = false
    }

    //val token: String? = authPreferences.getJwtToken()

    override fun showLoading() {
        _isLoading.value = true
    }

    override fun hideLoading() {
        _isLoading.value = false
    }


    init {
        if (!rememberMePreferences.isRememberMe()) {
            logout()
        }else{
            checkAuthState()
        }
    }


    private fun checkAuthState() {
        viewModelScope.launch {

            if (_isRefreshing.value) return@launch
            _isRefreshing.value = true

            try{
                Log.d("FlujoTest", "- Verificando el estado de la autenticación...")
                val shouldAutoLogin = rememberMePreferences.isRememberMe() && getCurrentUserUseCase() != null
                Log.d("FlujoTest", "sesionviewmodel -> shouldAutoLogin: $shouldAutoLogin")

                if (!shouldAutoLogin) {
                    Log.d("FlujoTest", " Auto login no permitido o sin usuario")
                    logout() // dentro del logout pones _authState = Unauthenticated
                    return@launch
                }

                val newTokenJWT = try {
                    checkAndRefreshTokenUseCase()
                } catch (e: Throwable) {
                    Log.e("FlujoTest", "Error al validar/refrescar token: ${e.message}", e)
                    null
                }

                if (newTokenJWT != null) {
                    Log.d("FlujoTest", "Token válido o refrescado: $newTokenJWT")
                    onUserAuthenticated()
                } else {
                    Log.w("FlujoTest", "⚠ Token nulo después de validación/refresh")
                    logout()
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun onUserAuthenticated() {
        _userRole.value = authPreferences.getUserRoleFromToken()
        _authState.value = AuthState.Authenticated

        authPreferences.getUserIdFromToken()?.let { uid ->
            loadUserInfo(uid)
        }
        loadInitialVehicles()
        checkPremiumStatus()
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
        }
    }

    /**
     * Borrar tokens de encryptedsharedpreferences, el rememberMe y hacer singout
     */
    fun logout() {
        Log.d("FlujoTest", "sesionviewmodel -> logout llamado...")
        rememberMePreferences.clearRememberMe()
        authPreferences.clearAllTokens()
        signOutUseCase()
        Log.d("FlujoTest", "Token shared pref: ${authPreferences.getJwtToken()}. Refreshtoken prefs ${authPreferences.getRefreshToken()}")
        _userRole.value = null
        _authState.value = AuthState.Unauthenticated
    }


    // Se llama desde el login/registro screen para cargar datos
    override fun onUserLoggedIn() {
        val userId = authPreferences.getUserIdFromToken()
        if (userId != null) {
            _authState.value = AuthState.Authenticated
            _userRole.value = authPreferences.getUserRoleFromToken()
            loadUserInfo(userId)
        }
    }


    fun checkPremiumStatus() {
        viewModelScope.launch {
            when(val result = isUserPremiumUseCase()) {
                is Resource.Success -> {
                    val newIsPremium = result.data
                    if (_isPremium.value != newIsPremium) {
                        _isPremium.value = newIsPremium
                    }
                }
                is Resource.Error -> {
                    Log.d("Flujotest", "Error comprobando IsPremium")
                }
            }
        }
    }


    /**
     * Activa el premium y guarda los nuevos tokens que devuelve la API. Se le pasa el token manualmente, en vez de dejarselo al interceptor ya que se envia desde otra Activity y da problemas.
     */
    fun activatePremiumUser(token: String) {
        Log.d("Flujotest", "Activando premium. token $token")
        viewModelScope.launch {
            when (val result = setPremiumUseCase("Bearer $token")) {
                is Resource.Success -> {
                    _isPremium.value = true
                    authPreferences.setJwtToken(result.data.token)
                    authPreferences.setRefreshToken(result.data.refreshToken)
                    _premiumActivated.value = true
                    Log.e("SessionViewModel", "Nuevo token y refresh token guardado. Token: ${result.data.token}. \nRefresh: ${result.data.refreshToken}")
                }
                is Resource.Error -> {
                    Log.e("SessionViewModel", "Error activando premium: ${result.message}")
                }
            }
        }
    }

    /* User info DRAWER */
    private fun loadUserInfo(userId: String) {
        viewModelScope.launch {
            when (val result = getUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    _userName.value = result.data.username
                    Log.e("Flujotest", "Useranme laoded correctly: ${result.data.username}")

                    // Cargar la imagen
                    loadProfileImage()

                    // Check premium
                    checkPremiumStatus()
                }
                is Resource.Error -> {
                        Log.e("Flujotest", "Error loading username: ${result.message}")
                    _userName.value = "TrackMyRide User"
                }
            }
        }
    }

    private fun loadProfileImage() {
        viewModelScope.launch {
            when(val result = getProfileImageUseCase()) {
                is Resource.Success -> {
                    _profileImageUrl.value = result.data.imageUrl
                }
                is Resource.Error -> {
                    Log.e("SessionViewModel", "Error loading profile image: ${result.message}")
                    _profileImageUrl.value = null
                }
            }
        }
    }

    fun updateUserName(newName: String) {
        _userName.value = newName
    }

    fun updateProfileImage(newUrl: String?) {
        _profileImageUrl.value = newUrl
    }

    /*
    Meto aqui los estados de la barra superior ya que este es un viewmodel generico que se peude usar en cualquier pantalla, por lo que me va perfecto para acceder a estos estados y modificarlos desde culauqier lugar
     */

    /* HOME SCREEN */

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


    /* ROUTE DETAILS SCREEN */

    private val _isEditingRouteDetails = MutableStateFlow(false)
    val isEditingRouteDetails: StateFlow<Boolean> = _isEditingRouteDetails

    fun toggleEditingRouteDetails() {
        _isEditingRouteDetails.value = !_isEditingRouteDetails.value
    }

    fun setEditingRouteDetails(isEditing: Boolean) {
        _isEditingRouteDetails.value = isEditing
    }


    /* My Profile SCREEN */

    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile: StateFlow<Boolean> = _isEditingProfile

    fun toggleEditingProfile() {
        _isEditingProfile.value = !_isEditingProfile.value
    }

    fun setEditingProfile(isEditing: Boolean) {
        _isEditingProfile.value = isEditing
    }


    /* Admin Screen */
    private val _refreshAdminTrigger = MutableSharedFlow<Unit>(replay = 0)
    val refreshAdminTrigger = _refreshAdminTrigger.asSharedFlow()

    fun triggerAdminRefresh() {
        viewModelScope.launch {
            _refreshAdminTrigger.emit(Unit)
        }
    }


    /* SNACKBAR */

    private val _uiSnackbar = MutableStateFlow<UiSnackbar?>(null)
    override val uiSnackbar: StateFlow<UiSnackbar?> = _uiSnackbar

    override fun showSnackbar(snackbar: UiSnackbar) {
        _uiSnackbar.value = snackbar
    }

    override fun dismissSnackbar() {
        _uiSnackbar.value = null
    }



}


