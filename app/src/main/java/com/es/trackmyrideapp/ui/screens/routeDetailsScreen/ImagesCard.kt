package com.es.trackmyrideapp.ui.screens.routeDetailsScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.es.trackmyrideapp.R


@Composable
fun ImagesCard(
    images: List<String>,
    onAddImage: () -> Unit,
    onImageClick: (String) -> Unit
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
        ImageGallery(
            images = images,
            modifier = Modifier.fillMaxWidth(),
            onAddImage = onAddImage,
            onImageClick = onImageClick
        )
    }
}


@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    images: List<String>,
    onAddImage: () -> Unit,
    onImageClick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            ,
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Images",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .padding(6.dp)
                        .height(36.dp),
                    contentPadding = PaddingValues(8.dp, 0.dp),
                    onClick = onAddImage,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add image",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Add image",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ImageScrollGallery(
                images = images,
                onItemClick = onImageClick
            )
        }
    }
}


@Composable
fun ImageScrollGallery(
    images: List<String>,
    onItemClick: (String) -> Unit,
    paddingScreen: Dp = 60.dp // Normalmente 30 a cada lado por defecto
) {
    if (images.isEmpty()){
        EmptyImagePlaceholder()
    }else{
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val itemPadding = 4.dp
        val itemCountVisible = 3
        val itemWidth = (screenWidth - paddingScreen - itemPadding * (itemCountVisible * 2)) / itemCountVisible

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(itemPadding)
        ) {
            items(images) { imageUrl ->
                ImageItem(
                    imageUrl = imageUrl,
                    onClick = { onItemClick(imageUrl) },
                    modifier = Modifier.width(itemWidth)
                )
            }
        }
    }

}

@Composable
fun ImageItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = colorResource(R.color.cardBorder),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}



@Composable
fun EmptyImagePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFBDBDBD)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No images available",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
@Preview
fun dffg(){
    ImageGallery(
        Modifier, listOf(""),{},{}
    )
}