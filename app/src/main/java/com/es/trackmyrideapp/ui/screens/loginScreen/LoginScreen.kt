package com.es.trackmyrideapp.ui.screens.loginScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAdminScreen: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val uiState by loginViewModel.uiState.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    // Navegar a home si Login exitoso
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val role = (uiState as LoginUiState.Success).role
            if (role == "ADMIN") {
                navigateToAdminScreen()
            } else {
                navigateToHome()
            }
        }
    }


    // Mostrar errores
    LaunchedEffect(errorMessage) {
        Log.d("LoginScreen", "errorMessage: $errorMessage")
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            loginViewModel.consumeErrorMessage()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding() // Paddings status + navigation bar
                .background(MaterialTheme.colorScheme.background)
        ){
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
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
                    onNavigateToForgotPassword = navigateToForgotPassword
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

        // SnackbarHost para mostrar el snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Indicador de carga
        if (uiState is LoginUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

