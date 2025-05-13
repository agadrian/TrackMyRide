package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.ui.screens.routeDetailsScreen.RouteDetailScreen


@Composable
fun IconTextRow(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    icon: ImageVector,
    fontSize: TextUnit = 16.sp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start // Nuevo parámetro
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = if (horizontalAlignment == Alignment.End) Arrangement.End else Arrangement.Start
            ) {
                // Columna de cada pack
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono y titulo
                    Row {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Texto
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = text,
                            fontWeight = FontWeight.Medium,
                            fontSize = fontSize,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun dfdsd(){
    RouteDetailScreen()
}