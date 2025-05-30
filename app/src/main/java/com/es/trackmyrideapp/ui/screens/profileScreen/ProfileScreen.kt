package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.ui.permissions.AppPermission
import com.es.trackmyrideapp.ui.permissions.ClosableBlockedDialog
import com.es.trackmyrideapp.ui.permissions.rememberPermissionHandler


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onPremiumScreenClicked: () -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uiState by profileViewModel.uiState.collectAsState()
    val uiMessage by profileViewModel.uiMessage.collectAsState()
    val focusManager = LocalFocusManager.current
    val sessionViewModel = LocalSessionViewModel.current
    val isPremium by sessionViewModel.isPremium.collectAsState()
    val isEditing by sessionViewModel.isEditingProfile.collectAsState()

    val email by profileViewModel.email
    val username by profileViewModel.username
    val savedUsername by profileViewModel.savedUsername
    val usernameError by profileViewModel.usernameError
    val phone by profileViewModel.phone
    val phoneError by profileViewModel.phoneError
    val memberSince by profileViewModel.memberSince

    val showDialog by profileViewModel.showChangePasswordDialog.collectAsState()
    val currentPassword by profileViewModel.currentPassword.collectAsState()
    val newPassword by profileViewModel.newPassword.collectAsState()
    val confirmPassword by profileViewModel.confirmPassword.collectAsState()

    val currentPasswordVisible by profileViewModel.currentPasswordVisible.collectAsState()
    val newPasswordVisible by profileViewModel.newPasswordVisible.collectAsState()
    val confirmPasswordVisible by profileViewModel.confirmPasswordVisible.collectAsState()

    val currentPasswordError by profileViewModel.currentPasswordError.collectAsState()
    val newPasswordError by profileViewModel.newPasswordError.collectAsState()
    val confirmPasswordError by profileViewModel.confirmPasswordError.collectAsState()

    val passwordDialogError  by profileViewModel.passwordDialogError.collectAsState()

    val profileImageUrl by profileViewModel.profileImageUrl.collectAsState()

    // Actualizar datos del usuario para el Drawer, gestionado en el sesssionviewmodel.
    LaunchedEffect(profileImageUrl) {
        profileImageUrl?.let {
            sessionViewModel.updateProfileImage(it)
        }
    }

    LaunchedEffect(username) {
        sessionViewModel.updateUserName(username)
    }


    LaunchedEffect(Unit){
        sessionViewModel.checkPremiumStatus()
    }


    /* Permisos para foto perfil */
    val (permissionState, requestPermission) = rememberPermissionHandler(
        permission = AppPermission.ReadImages
    )

    var showPermDialog by remember { mutableStateOf(false) }
    var shouldLaunchPicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileViewModel.uploadProfileImage(it)

        }
    }

    LaunchedEffect(shouldLaunchPicker, permissionState.isGranted) {
        if (shouldLaunchPicker) {
            if (permissionState.isGranted) {
                imagePickerLauncher.launch("image/*")
                shouldLaunchPicker = false
            } else {
                requestPermission()
            }
        }
    }

    LaunchedEffect(permissionState.isGranted) {
        if (permissionState.isGranted) {
            showPermDialog = false
        }
    }

    if (showPermDialog) {
        ClosableBlockedDialog(
            onDismiss = { showPermDialog = false },
            onResumeCheck = {
                requestPermission()
            }
        )
    }

    LaunchedEffect(permissionState.shouldShowBlockedDialog) {
        showPermDialog = permissionState.shouldShowBlockedDialog
    }


    // Mensajes snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg.message)
            profileViewModel.consumeUiMessage()
        }
    }

    // Resetear la edicion por si cambiamos de pantalla sin quitarla
    DisposableEffect(Unit) {
        onDispose {
            sessionViewModel.setEditingProfile(false)
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Image
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clickable {
                    shouldLaunchPicker = true
                }
            ,
        ) {
            if (profileImageUrl != null) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
            }


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
                text = savedUsername,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (!isPremium){
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
            }else{
                OutlinedButton(
                    modifier = Modifier
                        .padding(6.dp)
                        .height(36.dp),
                    contentPadding = PaddingValues(8.dp, 0.dp),
                    onClick = {}
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_premium),
                        contentDescription = "Premium",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Premium",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Text(
                text = "Member since $memberSince",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Body(
            email = email,
            username = username,
            phone = phone,
            onUsernameChanged = { profileViewModel.updateUsername(it) },
            onPhoneChanged = { profileViewModel.updatePhone(it) },
            onSaveButtonClicked = {
                if (profileViewModel.validateBeforeSave()) {
                    profileViewModel.updateProfile()
                    sessionViewModel.setEditingProfile(false)
                }},
            isEditing = isEditing,
            usernameError = usernameError,
            phoneError = phoneError,
            isSaveButtonEnabled = profileViewModel.validateAll(),
            showChangePasswordDialog = showDialog,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword,
            currentPasswordVisible = currentPasswordVisible,
            newPasswordVisible = newPasswordVisible,
            confirmPasswordVisible = confirmPasswordVisible,
            currentPasswordError = currentPasswordError,
            newPasswordError = newPasswordError,
            confirmPasswordError = confirmPasswordError,
            onCurrentPasswordChanged = { profileViewModel.currentPassword.value = it },
            onNewPasswordChanged = { profileViewModel.newPassword.value = it },
            onConfirmPasswordChanged = { profileViewModel.confirmPassword.value = it },
            onToggleCurrentPasswordVisibility = { profileViewModel.toggleCurrentPasswordVisibility() },
            onToggleNewPasswordVisibility = { profileViewModel.toggleNewPasswordVisibility() },
            onToggleConfirmPasswordVisibility = { profileViewModel.toggleConfirmPasswordVisibility() },
            onConfirmPasswordChange = { profileViewModel.changePassword() },
            onDismissPasswordDialog = { profileViewModel.resetPasswordDialogState() },
            onOpenPasswordDialog = {
                profileViewModel.openChangePasswordDialog()
            },
            generalError = passwordDialogError
        )
    }

    if (uiState is UiState.Loading) {
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
