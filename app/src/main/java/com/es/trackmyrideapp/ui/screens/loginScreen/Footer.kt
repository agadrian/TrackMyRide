package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.ui.components.CustomButton

@Composable
fun Footer(
    modifier: Modifier,
    onLoginButtonClicked: () -> Unit,
    onSingUpClicked: () -> Unit
){

    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        CustomButton(
            onclick = onLoginButtonClicked,
            text = "Login",
            buttonColor = MaterialTheme.colorScheme.primary,
            fontColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                color = MaterialTheme.colorScheme.secondary
            )

            TextButton(
                onClick = onSingUpClicked,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "Sign up",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}