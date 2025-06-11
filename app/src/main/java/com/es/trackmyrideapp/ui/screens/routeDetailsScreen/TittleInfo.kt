package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIcon
import com.es.trackmyrideapp.ui.components.VehicleIcon
import com.es.trackmyrideapp.ui.components.VehicleType
import com.es.trackmyrideapp.ui.components.getIcon
import com.es.trackmyrideapp.ui.components.getLabel

@Composable
fun TittleInfo(
    modifier: Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    date: String,
    vehicleType: VehicleType,
    isEditing: Boolean,
    titleError: String? = null
) {

    val isDarkMode = LocalIsDarkTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono del vehiculo
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.grayCircle).copy(alpha = if (isDarkMode) 0.6f else 1f)),
            contentAlignment = Alignment.Center
        ) {
            when (val icon = vehicleType.getIcon()) {
                is VehicleIcon.Vector -> {
                    Icon(
                        imageVector = icon.icon,
                        contentDescription = "${vehicleType.getLabel()} Icon",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
                is VehicleIcon.PainterIcon -> {
                    Icon(
                        painter = painterResource(id = icon.painter),
                        contentDescription = "${vehicleType.getLabel()} Icon",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Columna con título y fecha
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (!isEditing){
                // Título de la ruta
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }else{
                CustomTextFieldWithoutIcon(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Title",
                    value = title,
                    onValueChange = onTitleChange,
                    isError = titleError != null,
                    errorMessage = titleError
                )
            }

            // Fecha con icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Calendar",
                    modifier = Modifier.size(14.dp),
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = date,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Chip de tipo de transporte
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(R.color.grayCircle).copy(alpha = if (isDarkMode) 0.6f else 1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vehicleType.getLabel(),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
