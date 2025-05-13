package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.components.CustomTextField

@Composable
fun Body(
    email: String,
    username: String,
    phone: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChanged: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Personal Info",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            //Spacer(modifier = Modifier.height(16.dp))

            // Username
            CustomTextField(
                label = "Username",
                icon = Icons.Default.Person,
                value = username,
                onValueChange = onUsernameChanged
            )

            // Email
            CustomTextField(
                label = "Email",
                icon = Icons.Default.Email,
                value = email,
                onValueChange = onEmailChanged
            )

            // Phone
            CustomTextField(
                label = "Phone",
                icon = Icons.Default.Phone,
                value = phone,
                onValueChange = onPhoneChanged
            )
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

            TextField(
                value = password,
                onValueChange = onPasswordChanged,
                label = { Text("Password",fontSize = 14.sp)  },
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
                    unfocusedIndicatorColor = if (password.isEmpty()) Color.Gray else colorResource(
                        R.color.greenTextFieldFilled),
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(32.dp))

    CustomButton(
        onclick = {/* TODO: Accion de guardar la info */},
        text = "Save changes",
        buttonColor = MaterialTheme.colorScheme.primary,
        fontColor = colorResource(R.color.black)
    )
}




@Composable
@Preview
fun test233(){
    ProfileScreen(Modifier, {})
}