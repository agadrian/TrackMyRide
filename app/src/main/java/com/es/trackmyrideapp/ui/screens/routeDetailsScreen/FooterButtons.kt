package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton

@Composable
fun FooterButtons(
    onShareClicked: () -> Unit,
    onExportClicked: () -> Unit,
){
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CustomButton(
            modifier = Modifier.weight(0.5f),
            onclick = onShareClicked,
            text = "Share",
            buttonColor = MaterialTheme.colorScheme.primary,
            fontColor = colorResource(R.color.black),
            shape = 32.dp,
            icon = Icons.Default.Share,
            iconDescription = "Share"
        )

        CustomButton(
            modifier = Modifier.weight(0.5f),
            onclick = onExportClicked,
            text = "Export",
            buttonColor = MaterialTheme.colorScheme.primary,
            fontColor = colorResource(R.color.black),
            shape = 32.dp,
            icon = Icons.Default.Download,
            iconDescription = "Export"
        )
    }
}
