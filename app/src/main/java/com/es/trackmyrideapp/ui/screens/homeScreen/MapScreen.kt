package com.es.trackmyrideapp.ui.screens.homeScreen
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalSessionViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
@SuppressLint("MissingPermission") // El permiso ya llega aqui controlado
fun MapScreen(
    homeViewModel: HomeViewModel
) {
    val sessionViewModel = LocalSessionViewModel.current

    val cameraPositionState = rememberCameraPositionState()
    val mapType by sessionViewModel.mapType.collectAsState()
    val trackingState = homeViewModel.trackingState.value
    val routePoints = homeViewModel.routePoints.value
    val currentLocationState = homeViewModel.currentLocation.value
    val testPoints = homeViewModel.routePoints
    val simplifiedPoints = homeViewModel.simplifiedRoutePoints.value
    val testRoute = homeViewModel.routePointsTest.value


    val elapsedTime = homeViewModel.elapsedTime.value  // Tiempo transcurrido
    val distance = homeViewModel.getRouteDistance()  // Distancia total de la ruta
    val averageSpeed = homeViewModel.getRouteAverageSpeed()  // Velocidad promedio

    val context = LocalContext.current


    LaunchedEffect(Unit) {
        homeViewModel.loadGpxRoute(context)
    }


    // Efecto para obtener ubicación inicial si no hay ninguna
    LaunchedEffect(Unit) {
        Log.d("Tracking", "Obteniendo ubicación inicial")
        Log.d("Tracking", "Maptype: $mapType")
        if (currentLocationState == null) {
            homeViewModel.getLastKnownLocation()
        }
    }



    // Centrar mapa en la ubicación actual
    LaunchedEffect(currentLocationState) {
        currentLocationState?.let { location ->
            if (cameraPositionState.position != CameraPosition.fromLatLngZoom(location, 16f)) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 16f)
            }
        }
    }

    // Observamos cambios en routePoints
    LaunchedEffect(routePoints) {
        val lastPoint = routePoints.lastOrNull()
        if (lastPoint != null && trackingState) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(lastPoint),
                durationMs = 1000
            )
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                if (currentLocationState != null) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = true,
                            mapType = mapType
                        ),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = true,
                            zoomControlsEnabled = true
                        ),


                    ) {
                        // Test
                        //                Log.d("Tracking", "TestPoints: ${testPoints}")
                        //                if (testPoints.value.isNotEmpty()) {
                        //                    Polyline(
                        //                        points = testPoints.value,
                        //                        color = Color.Blue,
                        //                        width = 8f
                        //                    )
                        //                }

                        // Usamos routePoints.value para acceder a la lista
                        if (routePoints.size > 1) {
                            Polyline(
                                points = routePoints,
                                color = Color.Blue,
                                width = 8f
                            )
                        }


//                        Polyline(
//                            points = testRoute,
//                            color = Color.Red, // O cualquier otro color
//                            width = 6f
//                        )
                    }
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            // Información de la ruta
            if (trackingState) {
                InfoPanel(
                    elapsedTime = elapsedTime,
                    distance = distance,
                    averageSpeed = averageSpeed
                )
            }
        }
    }
}


@Composable
fun InfoPanel(
    elapsedTime: Long,
    distance: Double,
    averageSpeed: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
            .padding(top = 12.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(label = "Time", value = formatTime(elapsedTime))
        StatItem(label = "Distance", value = "%.2f km".format(distance / 1000))
        StatItem(label = "Avg Speed", value = "%.2f km/h".format(averageSpeed))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
    }
}
@SuppressLint("DefaultLocale")
fun formatTime(millis: Long): String {
    val minutes = (millis / 1000) / 60
    val seconds = (millis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Composable
fun TrackingButton(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel
) {
    val trackingState = homeViewModel.trackingState.value

    Box(
        modifier = modifier
            .size(50.dp)
            .background(Color.Red, shape = CircleShape)
            .clickable { homeViewModel.toggleTracking() },
        contentAlignment = Alignment.Center
    ) {
        val icon = if (trackingState) Icons.Default.Stop else Icons.Default.FiberManualRecord
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun TestButton(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel
) {
    val trackingState = homeViewModel.trackingState.value

    Box(
        modifier = modifier
            .size(40.dp)
            .background(Color.Blue, shape = CircleShape)
            .clickable { homeViewModel.generateSampleRoute() },
        contentAlignment = Alignment.Center
    ) {
        val icon = if (trackingState) Icons.Default.Stop else Icons.Default.FiberManualRecord
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}