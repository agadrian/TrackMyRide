package com.es.trackmyrideapp.ui.screens.registerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    snackbarHostState: SnackbarHostState
){
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val uiState by registerViewModel.uiState.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()


    // Navegar a Home si exitoso
    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            navigateToHome()
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
        modifier = modifier.fillMaxSize()
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
                    email = registerViewModel.email,
                    username = registerViewModel.username,
                    phone = registerViewModel.phone,
                    password = registerViewModel.password,
                    password2 = registerViewModel.password2,
                    onEmailChanged = { registerViewModel.updateEmail(it) },
                    onPasswordChanged = { registerViewModel.updatePassword(it) },
                    onPassword2Changed = { registerViewModel.updatePassword2(it) },
                    onUsernameChanged = { registerViewModel.updateUsername(it) },
                    onPhoneChanged = { registerViewModel.updatePhone(it) },
                    passwordVisible = registerViewModel.passwordVisible,
                    password2Visible = registerViewModel.password2Visible,
                    onPasswordVisibilityChanged = { registerViewModel.togglePasswordVisibility() },
                    onPassword2VisibilityChanged = { registerViewModel.togglePassword2Visibility() }
                )

                Spacer(modifier = Modifier.height(50.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Footer(
                modifier = Modifier,
                onRegisterButtonClicked = { registerViewModel.register() },
                onSingInClicked = navigateToLogin
            )
        }

        // SnackbarHost para mostrar el snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Indicador de carga
        if (uiState is RegisterUiState.Loading) {
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

