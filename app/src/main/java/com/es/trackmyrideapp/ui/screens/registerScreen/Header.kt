package com.es.trackmyrideapp.ui.screens.registerScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.R


@Composable
fun Header(
    lineColor: Color
){
    Box(
        Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_register),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
            ,
            contentDescription = "Register image",
            contentScale = ContentScale.Crop,
        )
    }

    Spacer(Modifier.height(10.dp))

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "Sign up",
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(10.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.3f)
        ) {
            // Subrayar con linea
            drawLine(
                color = lineColor,
                start = Offset.Zero,
                end = Offset(size.width / 1.6f, 0f),
                strokeWidth = 4f
            )
        }
    }
}


@Preview
@Composable
fun prepre(){
    Header(Color.Red)
}