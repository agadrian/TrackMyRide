package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton

@Composable
fun PremiumCard(
    onUpdateToPremiumClicked: () -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado Premium
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo_premium),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "Premium",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Característica: Compartir rutas
            PremiumFeatureItem(
                icon = Icons.Default.Share,
                title = "Share routes",
                description = "Share your favourite routes with friends",
                iconBackgroundColor = colorResource(R.color.orangeBackground),
                iconTint = colorResource(R.color.brownFontPremium)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Característica: Exportar rutas
            PremiumFeatureItem(
                icon = Icons.Default.Download,
                title = "Export routes",
                description = "Export your routes in your preferred format\nfor use on other devices or apps.",
                iconBackgroundColor = colorResource(R.color.orangeBackground),
                iconTint = colorResource(R.color.brownFontPremium)
            )

            Spacer(modifier = Modifier.height(24.dp))


            CustomButton(
                onclick = onUpdateToPremiumClicked,
                text = "Update to Premium",
                buttonColor = colorResource(R.color.orangeButton),
                fontColor = colorResource(R.color.black),
            )
        }
    }
}

@Composable
fun PremiumFeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    iconBackgroundColor: Color,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)
    ) {
        // Icono con fondo circular
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

