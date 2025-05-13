package com.es.trackmyrideapp.ui.screens.registerScreen

import android.widget.Toast
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
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginUiState


@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
){
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.registerUiState.collectAsState()
    val errorMessage by authViewModel.showErrorMessage.collectAsState()

    val context = LocalContext.current
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }


    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            navigateToHome()
        }
    }

    LaunchedEffect(errorMessage) {
        authViewModel.showErrorMessage.value?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.consumeErrorMessage()
        }
    }

    Column (
        modifier = modifier
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
                onEmailChanged = { email = it },
                onPasswordChanged = { password = it },
                onPassword2Changed = { password2 = it },
                onUsernameChanged = { username = it },
                onPhoneChanged = { phone = it },
                passwordVisible = passwordVisible,
                password2Visible = password2Visible,
                onPasswordVisibilityChanged = { passwordVisible = !passwordVisible },
                onPassword2VisibilityChanged = { password2Visible = !password2Visible },
            )

            Spacer(modifier = Modifier.height(50.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Footer(
            modifier = Modifier,
            onRegisterButtonClicked = {
                if (password != password2) {
                    Toast.makeText(context, "Passwords are not the same", Toast.LENGTH_SHORT).show()
                } else {
                    authViewModel.register(email, password)
                }
            },
            onSingInClicked = navigateToLogin
        )
    }

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


@Preview
@Composable
fun sdf(){
    RegisterScreen(Modifier,{},{})
}