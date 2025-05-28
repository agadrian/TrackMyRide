package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp),
                tint = MaterialTheme.colorScheme.secondary
            ) },
            modifier = modifier,
            singleLine = singleLine,
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = if (value.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (value.isEmpty()) Color.Gray else colorResource(R.color.greenTextFieldFilled),
                cursorColor = MaterialTheme.colorScheme.primary,
                errorLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface
            ),
            isError = isError && errorMessage != null
        )


        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun CustomTextFieldWithoutIcon(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(
                text = label,
                fontSize = 14.sp,
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

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}


@Composable
fun CustomTextFieldWithoutIconVehicles(
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