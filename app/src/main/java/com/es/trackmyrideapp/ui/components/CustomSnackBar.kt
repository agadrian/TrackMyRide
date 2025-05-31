package com.es.trackmyrideapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppSnackbarHost(
    uiSnackbar: UiSnackbar?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado para controlar la animación de salida
    var isVisible by remember { mutableStateOf(false) }

    // Controlar la visibilidad con animación
    LaunchedEffect(uiSnackbar) {
        if (uiSnackbar != null) {
            isVisible = true
        } else {
            isVisible = false
        }
    }

    // Animación de visibilidad
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        uiSnackbar?.let { snackbar ->
            // Auto-dismiss después de 5 segundos con animación
            LaunchedEffect(snackbar) {
                delay(5000L)
                // Primero activamos la animación de salida
                isVisible = false
                // Esperamos a que termine la animación antes de llamar onDismiss
                delay(350L)
                onDismiss()
            }

            val (backgroundColor, icon) = when (snackbar.messageType) {
                MessageType.INFO -> Pair(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    Icons.Filled.Info
                )
                MessageType.ERROR -> Pair(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                    Icons.Filled.Error
                )
            }

            // Animación para el icono
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "icon_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = backgroundColor.copy(alpha = 0.3f),
                        spotColor = backgroundColor.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Icono animado
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    )

                    // Mensaje
                    Text(
                        text = snackbar.message,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    // Acción o botón de cerrar
                    snackbar.actionLabel?.let { label ->
                        TextButton(
                            onClick = {
                                // Animación de salida manual
                                isVisible = false
                                // Esperamos la animación antes de llamar onDismiss
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(350L)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    } ?: if (snackbar.withDismissAction) {
                        IconButton(
                            onClick = {
                                // Animación de salida manual
                                isVisible = false
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(350L)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {

                    }
                }
            }
        }
    }
}