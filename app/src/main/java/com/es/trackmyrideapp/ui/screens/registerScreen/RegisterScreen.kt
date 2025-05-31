package com.es.trackmyrideapp.ui.screens.registerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.LocalSessionViewModel


@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAdminScreen: () -> Unit,
    snackbarHostState: SnackbarHostState
){
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val sessionViewModel = LocalSessionViewModel.current
    val uiState by registerViewModel.uiState.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()
    val focusManager = LocalFocusManager.current

    val email by registerViewModel.email
    val username by registerViewModel.username
    val phone by registerViewModel.phone
    val password by registerViewModel.password
    val password2 by registerViewModel.password2
    val passwordVisible by registerViewModel.passwordVisible
    val password2Visible by registerViewModel.password2Visible

    val emailError by registerViewModel.emailError
    val usernameError by registerViewModel.usernameError
    val phoneError by registerViewModel.phoneError
    val passwordError by registerViewModel.passwordError
    val password2Error by registerViewModel.password2Error

    val attemptSubmit by registerViewModel.attemptedSubmit

    // Navegar si exitoso
    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            sessionViewModel.onUserLoggedIn()
            val role = (uiState as RegisterUiState.Success).role
            if (role == "ADMIN") {
                navigateToAdminScreen()
            } else {
                navigateToHome()

            }
        }
    }

    // CircularProgessIndicator
    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Loading -> sessionViewModel.showLoading()
            else -> sessionViewModel.hideLoading()
        }
    }


    // Mostrar errores
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            registerViewModel.consumeErrorMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                // Evita que el click consuma otros eventos
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
    ){

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        ){
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Header(
                    lineColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(25.dp))


                Body(
                    email = email,
                    username = username,
                    phone = phone,
                    password = password,
                    password2 = password2,
                    onEmailChanged = { registerViewModel.updateEmail(it) },
                    onPasswordChanged = { registerViewModel.updatePassword(it) },
                    onPassword2Changed = { registerViewModel.updatePassword2(it) },
                    onUsernameChanged = { registerViewModel.updateUsername(it) },
                    onPhoneChanged = { registerViewModel.updatePhone(it) },
                    passwordVisible = passwordVisible,
                    password2Visible = password2Visible,
                    onPasswordVisibilityChanged = { registerViewModel.togglePasswordVisibility() },
                    onPassword2VisibilityChanged = { registerViewModel.togglePassword2Visibility() },
                    emailError = emailError,
                    usernameError = usernameError,
                    phoneError = phoneError,
                    passwordError = passwordError,
                    password2Error = password2Error,
                    attemptSubmit = attemptSubmit
                )

                Spacer(modifier = Modifier.height(50.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Footer(
                modifier = Modifier,
                onRegisterButtonClicked = {
                    registerViewModel.register()
                },
                onSingInClicked = navigateToLogin
            )
        }

        // SnackbarHost para mostrar el snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

