package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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