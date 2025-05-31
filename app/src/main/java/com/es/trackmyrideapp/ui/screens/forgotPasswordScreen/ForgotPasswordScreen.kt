package com.es.trackmyrideapp.ui.screens.forgotPasswordScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIcon
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val uiMessage by loginViewModel.uiMessage.collectAsState()
    val forgotPasswordUiState by loginViewModel.forgotPasswordUiState.collectAsState()
    val email by loginViewModel.emailForgotScreen
    val emailError by loginViewModel.emailForgotError
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            loginViewModel.consumeUiMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding()
            .clickable(
                // Evita que el click consuma otros eventos
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFieldWithoutIcon(
            label = "Please enter your email",
            value = email,
            onValueChange = { loginViewModel.updateEmaiForgotScreen(it) },
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError != null) {
            Text(
                text = emailError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { loginViewModel.sendPasswordReset(email) },
            enabled = forgotPasswordUiState != ForgotPasswordUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (forgotPasswordUiState == ForgotPasswordUiState.Loading) "Sending..." else "Send Email")
        }
    }
}