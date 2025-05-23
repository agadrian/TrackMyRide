package com.es.trackmyrideapp.ui.screens.routeDetailsScreen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton

@Composable
fun FooterButtons(
    onShareClicked: () -> Unit,
    onExportClicked: () -> Unit,
    //onImportClicked: (Uri) -> Unit
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

        /*
        ImportGpxButton(
            modifier = Modifier.weight(0.5f),
            { onImportClicked }
        )
*/

    }
}


@Composable
fun ImportGpxButton(
    modifier: Modifier,
    onImportClicked: (Uri) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Pedimos permiso persistente para leer después (opcional)
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // Llamamos al ViewModel para importar el GPX
            onImportClicked(uri)
        }
    }

    CustomButton(
        modifier = modifier,
        onclick = {
            launcher.launch(arrayOf("application/xml", "text/xml", "*/*"))
        },
        text = "Import",
        buttonColor = MaterialTheme.colorScheme.primary,
        fontColor = colorResource(R.color.black),
        shape = 32.dp,
        icon = Icons.Default.Download,
        iconDescription = "Import"
    )
}