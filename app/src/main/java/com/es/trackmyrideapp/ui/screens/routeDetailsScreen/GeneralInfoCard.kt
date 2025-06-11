package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.ui.components.CustomTextField
import com.es.trackmyrideapp.ui.components.IconTextBlock
import com.es.trackmyrideapp.ui.components.IconTextRow

@Composable
fun GeneralInfoCard(
    description: String,
    startTime : String,
    endTime: String,
    startPoint: String,
    endPoint: String,
    onDescriptionChanged: (String) -> Unit,
    isEditable: Boolean,
    descriptionError: String?
){
    val isDarkMode = LocalIsDarkTheme.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = if (isDarkMode) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
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
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "General Info",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Body(
                description = description,
                startTime = startTime,
                endTime = endTime,
                startPoint = startPoint,
                endPoint = endPoint,
                onDescriptionChanged = onDescriptionChanged,
                isEditable = isEditable,
                descriptionError = descriptionError
            )
        }
    }
}


@Composable
fun Body(
    description: String,
    onDescriptionChanged: (String) -> Unit,
    startTime: String,
    endTime: String,
    startPoint: String,
    endPoint: String,
    isEditable: Boolean,
    descriptionError: String?
){
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isEditable){
            // Desc
            Row(
                Modifier.fillMaxWidth(),
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Description",
                    icon = Icons.Outlined.Description,
                    value = description,
                    onValueChange = onDescriptionChanged,
                    enabled = true,
                    isError = descriptionError != null,
                    errorMessage = descriptionError
                )
            }
        }else{
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),

            ) {
                IconTextBlock(
                    modifier = Modifier,
                    title = "Description",
                    icon = Icons.Outlined.Description,
                    text = description.ifBlank { "No description" },
                )
            }
        }

        // Start time - end time
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement =  Arrangement.SpaceBetween
        ) {
            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Start time",
                icon = Icons.Outlined.WatchLater,
                text = startTime,
                horizontalAlignment = Alignment.Start
            )

            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "End time",
                icon = Icons.Outlined.WatchLater,
                text = endTime,
                horizontalAlignment = Alignment.End
            )
        }

        // Start point - end point
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement =  Arrangement.SpaceBetween
        ) {
            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "Start point",
                icon = Icons.Outlined.PinDrop,
                text = startPoint,
                fontSize = 14.sp,
                horizontalAlignment = Alignment.Start
            )

            IconTextRow(
                modifier = Modifier.weight(1f),
                title = "End point",
                icon = Icons.Outlined.PinDrop,
                text = endPoint,
                fontSize = 14.sp,
                horizontalAlignment = Alignment.End
            )
        }
    }
}
