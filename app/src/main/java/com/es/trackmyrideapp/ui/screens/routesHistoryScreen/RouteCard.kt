package com.es.trackmyrideapp.ui.screens.routesHistoryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.ui.components.getLabel

@Composable
fun RouteCard(
    tittle: String,
    date: String,
    distance: String,
    duration: String,
    pace: String,
    onViewDetailsClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    vehicleType: VehicleType,
){
    val isDarkMode = LocalIsDarkTheme.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(5.dp),
        elevation = if (isDarkMode) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column {
            // Header titulo y fecha
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = tittle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = date,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Chip de tipo de transporte
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorResource(R.color.grayCircle))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = vehicleType.getLabel(),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Body cajas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Distance box
                MetricBox(
                    icon = Icons.Outlined.Navigation,
                    label = "Distance",
                    value = distance,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Duration box
                MetricBox(
                    icon = Icons.Outlined.AccessTime,
                    label = "Duration",
                    value = duration,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Pace box
                MetricBox(
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    label = "Pace",
                    value = pace,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            // Footer ver detalles y delete icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onViewDetailsClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "View Details",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Details",
                        modifier = Modifier.padding(start = 4.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = onDeleteClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


@Composable
fun MetricBox(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(16.dp),
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

