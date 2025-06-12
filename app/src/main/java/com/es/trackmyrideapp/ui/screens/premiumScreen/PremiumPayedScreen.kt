package com.es.trackmyrideapp.ui.screens.premiumScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R

@Composable
fun PremiumActiveScreen(
    modifier: Modifier = Modifier
) {
    val isDarkMode = LocalIsDarkTheme.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .padding(horizontal = 30.dp)
                .padding(top = 16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Premium Active Card
            Card(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(5.dp),
                elevation = if (isDarkMode) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Premium Active Badge
                    Row(
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.orangeBackground),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colorResource(R.color.brownFontPremium),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PREMIUM ACTIVE",
                            color = colorResource(R.color.brownFontPremium),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Thank you for being a Premium member!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You are enjoying all exclusive features",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lifetime Access Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lifetime Access",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Benefits
            Text(
                text = "Your Premium Benefits",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Benefits List
            PremiumBenefitItem(
                icon = Icons.Default.Image,
                title = "Extra Images (+7)",
                description = "Upload up to 7 additional images (10 in total)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PremiumBenefitItem(
                icon = Icons.Default.History,
                title = "Complete Route History",
                description = "Access your entire history without limits"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PremiumBenefitItem(
                icon = Icons.Default.Layers,
                title = "3D View",
                description = "View your routes in 3D"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PremiumBenefitItem(
                icon = Icons.Default.Share,
                title = "Share Routes",
                description = "Share your routes with friends"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PremiumBenefitItem(
                icon = Icons.Default.Download,
                title = "Export Routes",
                description = "Download your routes in different formats"
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Actions
            Text(
                text = "Premium Actions",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PremiumActionButton(
                    icon = Icons.Default.Share,
                    text = "Share Routes",
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )

                PremiumActionButton(
                    icon = Icons.Default.Download,
                    text = "Export Routes",
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}