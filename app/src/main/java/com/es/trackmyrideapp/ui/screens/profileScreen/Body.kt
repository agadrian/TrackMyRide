package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.components.CustomTextField
import com.es.trackmyrideapp.ui.components.IconTextBlock

@Composable
fun Body(
    email: String,
    username: String,
    phone: String,
    onUsernameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSaveButtonClicked: () -> Unit,
    isEditing: Boolean = false,
    usernameError: String? = null,
    phoneError: String? = null,
    isSaveButtonEnabled: Boolean,
    showChangePasswordDialog: Boolean,
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    currentPasswordVisible: Boolean,
    newPasswordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    currentPasswordError: String?,
    newPasswordError: String?,
    confirmPasswordError: String?,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onConfirmPasswordChange: () -> Unit,
    onDismissPasswordDialog: () -> Unit,
    onOpenPasswordDialog: () -> Unit,
    generalError: String?
){
    // Personal Info
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = if (LocalIsDarkTheme.current) CardDefaults.cardElevation(defaultElevation = 0.dp)else CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Personal Info",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            //Spacer(modifier = Modifier.height(16.dp))

            // Username
            if (!isEditing){
                IconTextBlock(
                    title = "Username",
                    icon = Icons.Default.Person,
                    text = username
                )
            }else{
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Username",
                    icon = Icons.Default.Person,
                    value = username,
                    onValueChange = onUsernameChanged,
                    isError = usernameError != null,
                    errorMessage = usernameError
                )
            }

            // Email
            IconTextBlock(
                title = "Email",
                icon = Icons.Default.Email,
                text = email,
            )

            // Phone
            if (!isEditing){
                IconTextBlock(
                    title = "Phone",
                    icon = Icons.Default.Phone,
                    text = phone.ifEmpty { "Not set" },
                )
            }else{
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Phone",
                    icon = Icons.Default.Phone,
                    value = phone,
                    onValueChange = onPhoneChanged,
                    isError = phoneError != null,
                    errorMessage = phoneError
                )
            }
        }
    }


    Spacer(Modifier.height(32.dp))


    // Security
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = if (LocalIsDarkTheme.current) CardDefaults.cardElevation(defaultElevation = 0.dp)else CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            CustomButton(
                onclick = { onOpenPasswordDialog() },
                text = "Change Password",
                buttonColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                fontColor = colorResource(R.color.black),
                icon = Icons.Default.Lock,
                modifier = Modifier.padding(horizontal = 10.dp)
            )


            ChangePasswordDialog(
                showDialog = showChangePasswordDialog,
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword,
                currentPasswordVisible = currentPasswordVisible,
                newPasswordVisible = newPasswordVisible,
                confirmPasswordVisible = confirmPasswordVisible,
                onCurrentPasswordChanged = onCurrentPasswordChanged,
                onNewPasswordChanged = onNewPasswordChanged,
                onConfirmPasswordChanged = onConfirmPasswordChanged,
                onToggleCurrentPasswordVisibility = onToggleCurrentPasswordVisibility,
                onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
                onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                onConfirm = onConfirmPasswordChange,
                onDismiss = onDismissPasswordDialog,
                currentPasswordError = currentPasswordError,
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError,
                generalError = generalError
            )
        }
        Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(32.dp))

    if (isEditing){
        CustomButton(
            onclick = onSaveButtonClicked,
            text = "Save changes",
            buttonColor = MaterialTheme.colorScheme.primary,
            fontColor = colorResource(R.color.black),
            enabled = isSaveButtonEnabled
        )
    }
    Spacer(Modifier.height(16.dp))
}