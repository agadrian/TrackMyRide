package com.es.trackmyrideapp.ui.screens.forgotPasswordScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.core.states.UiSnackbar
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIcon
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val sessionViewModel = LocalSessionViewModel.current
    val uiMessage by loginViewModel.uiMessage.collectAsState()
    val forgotPasswordUiState by loginViewModel.forgotPasswordUiState.collectAsState()
    val email by loginViewModel.emailForgotScreen
    val emailError by loginViewModel.emailForgotError
    val focusManager = LocalFocusManager.current


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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .navigationBarsPadding()
            .imePadding() // Añade padding inferior de la misma altura que el teclado
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(128.dp))

            // Icono candado
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password Reset",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(32.dp))

            // Titulo
            Text(
                text = "Forgot Password?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitulo
            Text(
                text = "Don't worry! Enter your email address and we'll send you a link to reset your password.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))


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
                        //.align(Alignment.Start)
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
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}