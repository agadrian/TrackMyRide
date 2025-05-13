package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.R

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(
            text = label,
            fontSize = 14.sp
        ) },
        leadingIcon = { Icon(
            imageVector = icon,
            contentDescription = label,
        ) },
        modifier = modifier,
        singleLine = singleLine,
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            disabledIndicatorColor = if (value.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
            disabledContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = if (value.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun CustomTextFieldWithoutIcon(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        ) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = if (value.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
            cursorColor = MaterialTheme.colorScheme.primary
        ),

    )
}