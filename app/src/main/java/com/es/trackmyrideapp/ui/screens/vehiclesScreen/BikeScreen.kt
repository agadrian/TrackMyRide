package com.es.trackmyrideapp.ui.screens.vehiclesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomTextFieldWithoutIconVehicles

@Composable
fun BikeScreen(
    icon: ImageVector,
    vehicleLabel: String,
    name: String,
    brand: String,
    model: String,
    year: String,
    bikeType: String,
    notes: String,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onBikeTypeChange: (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        // General Info
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = if (LocalIsDarkTheme.current) CardDefaults.cardElevation(defaultElevation = 0.dp)else CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                //verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (LocalIsDarkTheme.current) MaterialTheme.colorScheme.surfaceVariant else colorResource(R.color.grayCircle))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Icon",
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Basic Data",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = "General info about your $vehicleLabel",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BodyBike(
                    name = name,
                    brand = brand,
                    model = model,
                    year = year,
                    bikeType = bikeType,
                    notes = notes,
                    onNameChange = onNameChange,
                    onBrandChange = onBrandChange,
                    onModelChange = onModelChange,
                    onYearChange = onYearChange,
                    onBikeTypeChange = onBikeTypeChange,
                    onNotesChange = onNotesChange
                )

                Spacer(Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun BodyBike(
    name: String,
    brand: String,
    model: String,
    year: String,
    notes: String,
    bikeType: String,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onBikeTypeChange : (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Name
            CustomTextFieldWithoutIconVehicles(
                label = "Name",
                value = name,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onNameChange
            )
        }


        // Row brand y model
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextFieldWithoutIconVehicles(
                label = "Brand",
                value = brand,
                modifier = Modifier.weight(0.5f),
                onValueChange = onBrandChange
            )

            CustomTextFieldWithoutIconVehicles(
                label = "Model",
                value = model,
                modifier = Modifier.weight(0.5f),
                onValueChange = onModelChange
            )
        }


        // Row year y bike type
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextFieldWithoutIconVehicles(
                label = "Year",
                value = year,
                modifier = Modifier.weight(0.5f),
                onValueChange = onYearChange
            )

            CustomTextFieldWithoutIconVehicles(
                label = "Bike type",
                value = bikeType,
                modifier = Modifier.weight(0.5f),
                onValueChange = onBikeTypeChange
            )
        }

        Row(Modifier.fillMaxWidth()) {
            // Aditional notes
            CustomTextFieldWithoutIconVehicles(
                label = "Additional Notes",
                value = notes,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                onValueChange = onNotesChange
            )
        }
    }
}


