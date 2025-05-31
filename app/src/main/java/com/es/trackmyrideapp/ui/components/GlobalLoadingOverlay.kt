package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

@Composable
fun GlobalLoadingOverlay(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = false) {} // Bloquea interacciones debajo
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("loading_indicator")
                ,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}