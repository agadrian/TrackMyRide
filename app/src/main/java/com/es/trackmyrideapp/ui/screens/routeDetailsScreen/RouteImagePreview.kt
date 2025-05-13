package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton

@Composable
fun RouteImagePreview(
    onFullMapClicked: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.map_preview_img),
            contentDescription = "Image Default Map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        CustomButton(
            modifier = Modifier.width(150.dp).align(Alignment.Center),
            onclick = onFullMapClicked,
            text = "Full Map",
            fontColor = colorResource(R.color.black),
            buttonColor = colorResource(R.color.white),
            shape = 32.dp,
            icon = Icons.Default.RemoveRedEye,
            iconDescription = "Full Map Button",
            fontSize = 16.sp,
            iconSize = 22.dp
        )
    }
}

@Composable
fun DialogMap(
    onDismissRequest: () -> Unit
){
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            // Botón de cerrar (X)
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Contenido del modal (ahora vacío, luego ira el mapa)
            Text(
                text = "Aquí irá el mapa...",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}