package com.es.trackmyrideapp.ui.screens.loginScreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.data.local.RememberMePreferences
import com.es.trackmyrideapp.domain.usecase.auth.SendPasswordResetUseCase
import com.es.trackmyrideapp.domain.usecase.auth.SignInUseCase
import com.es.trackmyrideapp.ui.screens.forgotPasswordScreen.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


interface ILoginViewModel {
    val email: String
    val password: String
    val passwordVisible: Boolean
    val rememberMe: Boolean
    val emailError: MutableState<String?>
    val passwordError: MutableState<String?>
    val attemptedSubmit: MutableState<Boolean>

    val uiState: StateFlow<LoginUiState>
    val uiMessage: StateFlow<UiMessage?>

    fun updateEmail(newEmail: String)
    fun updatePassword(newPassword: String)
    fun togglePasswordVisibility()
    fun toggleRememberMe()
    fun signIn()
    fun consumeUiMessage()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val rememberMePreferences: RememberMePreferences,
    private val authPreferences: AuthPreferences,
) : ViewModel(), ILoginViewModel {

    // Estados del formulario
    override var email by mutableStateOf("")
        private set
    override var password by mutableStateOf("")
        private set
    override var passwordVisible by mutableStateOf(false)
        private set
    override var rememberMe by mutableStateOf(false)
        private set

    // UI State
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val uiState: StateFlow<LoginUiState> = _uiState

    // Ui message
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    override val uiMessage: StateFlow<UiMessage?> = _uiMessage

    override fun consumeUiMessage() {
        _uiMessage.value = null
    }

    private val _forgotPasswordUiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val forgotPasswordUiState: StateFlow<ForgotPasswordUiState> = _forgotPasswordUiState

    // Funciones para actualizar los estados
    override fun updateEmail(newEmail: String) { email = newEmail }
    override fun updatePassword(newPassword: String) { password = newPassword }
    override fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    override fun toggleRememberMe() { rememberMe = !rememberMe }

    // LOGIN
    override fun signIn() {
        attemptedSubmit.value = true
        if (!validateAll()) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = signInUseCase(email, password)

            Log.d("FlujoTest", "authviewmodel: signInUseCase llamado")

            result.fold(
                onSuccess = {
                    if (rememberMe) rememberMePreferences.setRememberMe(true)

                    Log.d("FlujoTest", "authviewmodel: signInUseCase onsucces")

                    val role = authPreferences.getUserRoleFromToken()

                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onsucces.setJwtToken y setrefresh llamados ")

                    _uiState.value = LoginUiState.Success(role)

                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onsucces. loginuiState: ${_uiState.value} ")

                    Log.d("JWT Token", "Login uisate: ${_uiState.value}")
                },
                onFailure = { exception ->
                    _uiMessage.value = (UiMessage(exception.message ?: "Unknown error. Try Again Later", MessageType.ERROR))
                    Log.d("FlujoTest", "authviewmodel: signInUseCase llamado onfailure. exception: ${exception.message} ")
                    _uiState.value = LoginUiState.Idle
                }
            )
        }
    }

    override val emailError = mutableStateOf<String?>(null)
    override val passwordError = mutableStateOf<String?>(null)
    override val attemptedSubmit = mutableStateOf(false)

    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return "Email cannot be empty"
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return if (!pattern.matcher(value).matches()) "Invalid email address" else null
    }

    private fun validatePassword(value: String): String? {
        return if (value.isBlank()) "Password cannot be empty"
        else null
    }

    private fun validateAll(): Boolean {
        emailError.value = validateEmail(email)
        passwordError.value = validatePassword(password)
        return emailError.value == null && passwordError.value == null
    }

    //  FORGOT PASSWORD

    private val _emailForgotScreen = mutableStateOf("")
    val emailForgotScreen: State<String> get() = _emailForgotScreen

    fun updateEmaiForgotScreen(newEmail: String) {
        _emailForgotScreen.value = newEmail
    }

    private val _emailForgotError = mutableStateOf<String?>(null)
    val emailForgotError: State<String?> get() = _emailForgotError

    fun sendPasswordReset(email: String) {

        if (email.isBlank()) {
            _emailForgotError.value = "Email cannot be empty"
            return
        }

        _emailForgotError.value = null

        viewModelScope.launch {
            _forgotPasswordUiState.value = ForgotPasswordUiState.Loading

            val result = sendPasswordResetUseCase(email)

            result.fold(
                onSuccess = {
                    _forgotPasswordUiState.value = ForgotPasswordUiState.Success
                    _uiMessage.value = (UiMessage("Password reset email sent", MessageType.INFO))
                },
                onFailure = {
                    _forgotPasswordUiState.value = ForgotPasswordUiState.Idle
                    _uiMessage.value = (UiMessage(it.message ?: "Error during reseting your password. Try again later...", MessageType.ERROR))
                }
            )
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordUiState.value = ForgotPasswordUiState.Idle
    }

}