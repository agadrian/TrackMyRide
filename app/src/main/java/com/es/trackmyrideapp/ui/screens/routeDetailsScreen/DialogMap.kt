package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.domain.model.RoutePin
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DialogMap(
    onDismissRequest: () -> Unit,
    routePoints: List<LatLng>,
    startPointName: String,
    endPointName: String,
    customPins: List<RoutePin> = emptyList(),
    showAddPinDialog: Boolean,
    newPinPosition: LatLng?,
    pinTitle: String,
    pinDescription: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismissAddPinDialog: () -> Unit,
    onAddPin: () -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    onDeletePin: (RoutePin) -> Unit,
    titleError: String? = null,
    descriptionError: String? = null
){

    var selectedPin by remember { mutableStateOf<RoutePin?>(null) }
    var hasCenteredOnRoute by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    LaunchedEffect(routePoints) {
        if (!hasCenteredOnRoute && routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(routePoints.first(), 16f)
            )
            hasCenteredOnRoute = true
        }
    }

    LaunchedEffect(selectedPin) {
        selectedPin?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    16f
                )
            )
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(top = 64.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Full Map View",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )


                        IconButton(
                            onClick = onDismissRequest,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (routePoints.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        ) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(
                                            bottomStart = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    ),
                                cameraPositionState = cameraPositionState,
                                onMapLongClick = onMapLongClick
                            ) {
                                Polyline(
                                    points = routePoints,
                                    color = Color.Blue,
                                    width = 6f
                                )
                                Marker(
                                    state = MarkerState(position = routePoints.first()),
                                    title = "Start point",
                                    snippet = startPointName.ifEmpty { "Start point" }
                                )
                                Marker(
                                    state = MarkerState(position = routePoints.last()),
                                    title = "End point",
                                    snippet = endPointName.ifEmpty { "End point" }
                                )

                                customPins.forEach { pin ->
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(
                                                pin.latitude,
                                                pin.longitude
                                            )
                                        ),
                                        title = pin.title,
                                        snippet = pin.description,
                                        onClick = {
                                            selectedPin = pin
                                            false
                                        },
                                        onInfoWindowClose = {
                                            selectedPin = null
                                        }
                                    )
                                }
                            }

                            // Box con el icono para borrar el pin
                            selectedPin?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter)
                                        .background(colorResource(id = R.color.delete_background))
                                        .drawBehind {
                                            // Dibujar línea solo en el lado izquierdo
                                            drawLine(
                                                color = Color(ContextCompat.getColor(context, R.color.delete_border)),
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, size.height),
                                                strokeWidth = 12.dp.toPx()
                                            )
                                        }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        // Textos
                                        Column(
                                            modifier = Modifier.padding(start = 8.dp)

                                        ) {
                                            Text(
                                                text = "Delete '${it.title}'?",
                                                fontWeight = FontWeight.Medium,
                                                color = colorResource(id = R.color.delete_text_primary),
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "This action cannot be undone",
                                                color = colorResource(id = R.color.delete_text_secondary),
                                                fontSize = 12.sp
                                            )
                                        }


                                        // Botón de eliminar
                                        Button(
                                            onClick = {
                                                onDeletePin(it)
                                                selectedPin = null
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(id = R.color.delete_button),
                                                contentColor = colorResource(id = R.color.white)
                                            ),
                                            modifier = Modifier.size(40.dp),
                                            contentPadding = PaddingValues(0.dp),
                                            shape = CircleShape
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete pin",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showAddPinDialog && newPinPosition != null) {
                            AddPinDialog(
                                title = pinTitle,
                                onTitleChange = onTitleChange,
                                description = pinDescription,
                                onDescriptionChange = onDescriptionChange,
                                onDismiss = onDismissAddPinDialog,
                                onConfirm = onAddPin,
                                titleError = titleError,
                                descriptionError = descriptionError
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sorry, no route available, try later...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}