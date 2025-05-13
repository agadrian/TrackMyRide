package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    password: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChanged: () -> Unit,
    rememberMe: Boolean,
    onRememberMeChanged: (Boolean) -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {

        CustomTextField(
            label = "Email",
            icon = Icons.Default.Email,
            value = email,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onEmailChanged
        )


        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange =onRememberMeChanged,
                )
                Text(
                    text = "Remember Me",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            TextButton(onClick = onNavigateToForgotPassword) {
                Text(
                    "Forgot Password?",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun preview() {
    Body(
        "", "", {}, {}, false, {}, false, {}, {}
    )
}