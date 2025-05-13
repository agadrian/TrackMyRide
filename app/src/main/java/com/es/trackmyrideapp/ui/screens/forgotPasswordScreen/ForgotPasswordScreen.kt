package com.es.trackmyrideapp.ui.screens.forgotPasswordScreen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.ui.AuthViewModel
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIcon
import com.es.trackmyrideapp.ui.screens.loginScreen.LoginViewModel
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val errorMessage by authViewModel.showErrorMessage.collectAsState()
    val forgotPasswordUiState by authViewModel.forgotPasswordUiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(forgotPasswordUiState) {
        when (forgotPasswordUiState) {
            is ForgotPasswordUiState.Success -> {
                Toast.makeText(context, "Email sent", Toast.LENGTH_LONG).show()
                authViewModel.resetForgotPasswordState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.consumeErrorMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding(),
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
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    authViewModel.sendPasswordReset(email)
                } else {
                    Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = forgotPasswordUiState != ForgotPasswordUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (forgotPasswordUiState == ForgotPasswordUiState.Loading) "Sending..." else "Send Email")
        }
    }
}