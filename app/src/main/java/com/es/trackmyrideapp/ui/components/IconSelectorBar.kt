package com.es.trackmyrideapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R

@Composable
fun <T> IconSelectorBar(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemIcon: @Composable (T) -> Unit,
    paddingScreen: Dp,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemPadding = 4.dp
    val itemWidth = (screenWidth - paddingScreen - itemPadding * (items.count() * 2)) / 3f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                RoundedCornerShape(50)
            )
            .padding(itemPadding)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                Button(
                    onClick = { onItemSelected(item) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = colorResource(R.color.black)
                    ),
                    elevation = null,
                    modifier = Modifier
                        .width(itemWidth)
                        .height(40.dp)

                ) {
                    itemIcon(item)
                }
            }
        }
    }
}