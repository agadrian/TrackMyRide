package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.ui.components.IconTextRow

@Composable
fun StatsCard(
    totalDistance: String,
    movingTime: String,
    avgSpeed: String,
    maxSpeed: String,
    fuelConsumed: String,
    efficiency: String
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                Modifier.fillMaxWidth()
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QueryStats,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Stats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            Body(
                totalDistance = totalDistance,
                movingTime = movingTime,
                avgSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                fuelConsumed = fuelConsumed,
                efficiency = efficiency
            )
        }
    }
}

@Composable
fun Body(
    totalDistance: String,
    movingTime: String,
    avgSpeed: String,
    maxSpeed: String,
    fuelConsumed: String,
    efficiency: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Total distance - Moving time
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Total distance",
                icon = Icons.Outlined.Straighten,
                text = totalDistance,
                horizontalAlignment = Alignment.Start
            )


            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Moving time",
                icon = Icons.Outlined.Schedule,
                text = movingTime,
                horizontalAlignment = Alignment.End
            )

        }

        // Avg speed - Max speed
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Avg Speed",
                icon = Icons.Outlined.Speed,
                text = avgSpeed,
                horizontalAlignment = Alignment.Start
            )



            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Max Speed",
                icon = Icons.Outlined.Bolt,
                text = maxSpeed,
                horizontalAlignment = Alignment.End
            )

        }


        // Fuel consumed - Efficiemcy
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Fuel Consumed",
                icon = Icons.Outlined.LocalGasStation,
                text = fuelConsumed,
                horizontalAlignment = Alignment.Start
            )

            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Efficiency",
                icon = Icons.AutoMirrored.Outlined.TrendingUp,
                text = efficiency,
                horizontalAlignment = Alignment.End
            )
        }
    }
}


@Composable
@Preview
fun dfdsd(){
    RouteDetailScreen()
}