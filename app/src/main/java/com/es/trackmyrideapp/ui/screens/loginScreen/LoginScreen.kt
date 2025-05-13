package com.es.trackmyrideapp.ui.screens.loginScreen

import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.ui.AuthViewModel


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.loginUiState.collectAsState()
    val errorMessage by authViewModel.showErrorMessage.collectAsState()

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }




    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            navigateToHome()
        }
    }

    LaunchedEffect(errorMessage) {
        authViewModel.showErrorMessage.value?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.consumeErrorMessage()
        }
    }

    Column(
        modifier = modifier
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
                email = email,
                password = password,
                onEmailChanged = { email = it },
                onPasswordChanged = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChanged = { passwordVisible = !passwordVisible },
                rememberMe = rememberMe,
                onRememberMeChanged = { rememberMe = it },
                onNavigateToForgotPassword = navigateToForgotPassword
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        Spacer(modifier = Modifier.weight(1f))

        Footer(
            modifier = Modifier,
            onLoginButtonClicked = {
                authViewModel.signIn(email, password, rememberMe)
                /*TODO: Hacer que inicie sesion. Mientra inicia, mostrar un circulo girando con el fondo grisaceo, y si esta bien, navegar al home */
            },
            onSingUpClicked = navigateToRegister,

        )
    }


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


@Preview
@Composable
fun dfd (){
    LoginScreen(
        modifier = Modifier, {}, {}, {}
    )
}