package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.R


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onPremiumScreenClicked: () -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val confirmationMessage by profileViewModel.confirmationMessage.collectAsState()
    val uiState by profileViewModel.uiState.collectAsState()

    // Mostrar errores
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Error) {
            val errorMessage = (uiState as ProfileUiState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    // Mensajes de confirmacion
    LaunchedEffect(confirmationMessage) {
        confirmationMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            profileViewModel.consumeConfirmationMessage()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Image
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Transparent, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }


        Spacer(Modifier.height(8.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = profileViewModel.username,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                modifier = Modifier
                    .padding(6.dp)
                    .height(36.dp),
                contentPadding = PaddingValues(8.dp, 0.dp),
                onClick = onPremiumScreenClicked
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_premium),
                    contentDescription = "Go Premium",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Go Premium",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "Member since ${profileViewModel.memberSince}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Body(
            email = profileViewModel.email,
            username = profileViewModel.username,
            phone = profileViewModel.phone,
            password = profileViewModel.password,
            onEmailChanged = { /* No editable */ },
            onUsernameChanged = { profileViewModel.updateUsername(it) },
            onPhoneChanged = { profileViewModel.updatePhone(it) },
            onPasswordChanged = { /* No usado directamente */ },
            passwordVisible = profileViewModel.passwordVisible,
            onPasswordVisibilityChanged = { profileViewModel.togglePasswordVisibility() },
            onSaveButtonClicked = { if (profileViewModel.validateBeforeSave()) profileViewModel.updateProfile() }
            //onPasswordEditClicked = { profileViewModel.openChangePasswordDialog() }
        )


    }
}
