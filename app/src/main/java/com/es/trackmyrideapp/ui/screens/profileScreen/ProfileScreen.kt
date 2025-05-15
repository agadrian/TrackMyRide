package com.es.trackmyrideapp.ui.screens.profileScreen

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.R


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onPremiumScreenClicked: () -> Unit
) {

    val profileViewModel: ProfileViewModel = hiltViewModel()

    val uiState by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current


    // Mostrar errores
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Error) {
            val errorMessage = (uiState as ProfileViewModel.ProfileUiState.Error).message
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
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
                text = "Adrian Arroyo",
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
                text = "Member since 2021",
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

@Composable
@Preview
fun test2(){
    ProfileScreen(Modifier, {})
}