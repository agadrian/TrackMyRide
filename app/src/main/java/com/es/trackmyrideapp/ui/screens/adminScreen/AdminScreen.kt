package com.es.trackmyrideapp.ui.screens.adminScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.ui.components.ConfirmationDialog

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
){
    val adminViewModel: AdminViewModel = hiltViewModel()
    val uiState by adminViewModel.uiState.collectAsState()
    val uiMessage by adminViewModel.uiMessage.collectAsState()
    var userToDelete by remember { mutableStateOf<User?>(null) }

    if (userToDelete != null) {
        ConfirmationDialog(
            title = "Delete User",
            message = "Are you sure you want to delete ${userToDelete!!.username}?",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            onConfirm = {
                adminViewModel.deleteUser(userToDelete!!.id)
            },
            onDismiss = {
                userToDelete = null
            }
        )
    }


    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message.message,
                withDismissAction = message.type == MessageType.ERROR,
                duration = SnackbarDuration.Short
            )
            adminViewModel.consumeUiMessage()
        }
    }


    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 30.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Admin Panel",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
            }

            when (uiState) {
                is AdminUiState.Success -> {
                    val users = (uiState as AdminUiState.Success).users

                    items(users) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = user.username, style = MaterialTheme.typography.titleMedium)
                                    Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = if (user.isPremium) "Premium" else "Free",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (user.isPremium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { adminViewModel.togglePremium(user.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.isPremium) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (user.isPremium) "Ungrant" else "Grant"
                                        )
                                    }

                                    IconButton(
                                        // Llamar a confirmacion de delete
                                        onClick = { userToDelete = user }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete User",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is AdminUiState.Idle -> {
                    item {
                        Text(
                            text = "Loading Users...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                else -> { /* Nada */ }
            }
        }

        // Indicador de carga
        if (uiState is AdminUiState.Loading) {
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
}