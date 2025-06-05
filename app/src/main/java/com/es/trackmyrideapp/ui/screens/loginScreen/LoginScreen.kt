package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.ui.viewmodels.ISessionViewModel


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAdminScreen: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    loginViewModel: ILoginViewModel = hiltViewModel<LoginViewModel>(),
    sessionViewModel: ISessionViewModel = LocalSessionViewModel.current
) {
    //val loginViewModel: LoginViewModel = hiltViewModel()
    //val sessionViewModel = LocalSessionViewModel.current
    val uiState by loginViewModel.uiState.collectAsState()
    val uiMessage by loginViewModel.uiMessage.collectAsState()
    val focusManager = LocalFocusManager.current
    val emailError by loginViewModel.emailError
    val passwordError by loginViewModel.passwordError
    val attemptedSubmit by loginViewModel.attemptedSubmit

    // CircularProgessIndicator
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Loading -> sessionViewModel.showLoading()
            else -> sessionViewModel.hideLoading()
        }
    }

    // Navegar a home/admin si Login exitoso
    LaunchedEffect(uiState) {
        sessionViewModel.onUserLoggedIn()
        if (uiState is LoginUiState.Success) {
            val role = (uiState as LoginUiState.Success).role
            if (role == "ADMIN") {
                navigateToAdminScreen()
            } else {
                navigateToHome()
            }
        }
    }

    // Snackbar msg
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            sessionViewModel.showSnackbar(
                UiSnackbar(
                    message = message.message,
                    messageType = message.type,
                    withDismissAction = true
                )
            )
            loginViewModel.consumeUiMessage()
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
            }
            .systemBarsPadding(),
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .background(MaterialTheme.colorScheme.background)
        ){
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Header(
                    lineColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(30.dp))


                Body(
                    email = loginViewModel.email,
                    password = loginViewModel.password,
                    onEmailChanged = { loginViewModel.updateEmail(it) },
                    onPasswordChanged = { loginViewModel.updatePassword(it) },
                    passwordVisible = loginViewModel.passwordVisible,
                    onPasswordVisibilityChanged = { loginViewModel.togglePasswordVisibility() },
                    rememberMe = loginViewModel.rememberMe,
                    onRememberMeChanged = { loginViewModel.toggleRememberMe() },
                    onNavigateToForgotPassword = navigateToForgotPassword,
                    emailError = emailError,
                    passwordError = passwordError,
                    attemptedSubmit = attemptedSubmit
                )

                Spacer(modifier = Modifier.height(16.dp))

            }

            Spacer(modifier = Modifier.weight(1f))

            Footer(
                modifier = Modifier,
                onLoginButtonClicked = { loginViewModel.signIn() },
                onSingUpClicked = navigateToRegister
            )
        }
    }
}

