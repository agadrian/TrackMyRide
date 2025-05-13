package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.ui.components.CustomTextField
import com.es.trackmyrideapp.ui.components.IconTextRow

@Composable
fun GeneralInfoCard(
    description: String,
    startTime : String,
    endTime: String,
    startPoint: String,
    endPoint: String,
    onDescriptionChanged: (String) -> Unit
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
                onDescriptionChanged = onDescriptionChanged
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
    endPoint: String
){
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Desc
        Row(
            Modifier.fillMaxWidth(),
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Description",
                icon = Icons.Default.Description,
                value = description,
                onValueChange = onDescriptionChanged,
                enabled = false
            )
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

@Composable
@Preview
fun dfsd(){
    RouteDetailScreen()
}