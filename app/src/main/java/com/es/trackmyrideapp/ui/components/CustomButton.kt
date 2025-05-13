package com.es.trackmyrideapp.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onclick: () -> Unit,
    text: String,
    buttonColor: Color,
    fontColor: Color,
    fontSize: TextUnit = 18.sp,
    shape: Dp = 12.dp,
    icon: ImageVector? = null,
    iconDescription: String? = null,
    iconSize: Dp? = 24.dp

    ){
    Button(
        onClick = onclick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = fontColor
        ),
        shape = RoundedCornerShape(shape),

    ) {

        if (icon != null){

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription,
                    modifier = Modifier.size(iconSize ?: 24.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                )
            }

        }else{
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
            )
        }

    }
}