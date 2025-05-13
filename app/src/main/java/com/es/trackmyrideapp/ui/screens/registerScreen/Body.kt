package com.es.trackmyrideapp.ui.screens.registerScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomTextField

@Composable
fun Body(
    email: String,
    username: String,
    password: String,
    password2: String,
    phone: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPassword2Changed: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    passwordVisible: Boolean,
    password2Visible: Boolean,
    onPasswordVisibilityChanged: () -> Unit,
    onPassword2VisibilityChanged: () -> Unit,

) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {

        // Email
        CustomTextField(
            label = "Email",
            icon = Icons.Default.Email,
            value = email,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onEmailChanged
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Username
        CustomTextField(
            label = "Username",
            icon = Icons.Default.Person,
            value = username,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onUsernameChanged
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Phone
        CustomTextField(
            label = "Phone",
            icon = Icons.Default.Phone,
            value = phone,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onPhoneChanged
        )


        Spacer(modifier = Modifier.height(15.dp))


        // Password
        TextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(
                    onClick = onPasswordVisibilityChanged
                ) {
                    Icon(image, contentDescription = "Toggle Password Visibility")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (password.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Password2
        TextField(
            value = password2,
            onValueChange = onPassword2Changed,
            label = { Text("Confirm password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            trailingIcon = {
                val image = if (password2Visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(
                    onClick = onPassword2VisibilityChanged
                ) {
                    Icon(image, contentDescription = "Toggle Password Visibility")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (password2.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview
@Composable
fun dfgdsg(){
    Body(
        "", "", "", "", "", {}, {}, {}, {}, {}, false,false, {},{}
    )
}