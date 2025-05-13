package com.es.trackmyrideapp.ui.screens.aboutUsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R


@Composable
fun Body(){
    Column(
        Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Key Features",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))


        FeatureItem(
            number = 1,
            title = "Route Tracking",
            desc = "Record your routes with detailed statistics and maps"
        )
        FeatureItem(
            number = 2,
            title = "Vehicle Management",
            desc = "Track multiple vehicles and their performance"
        )
        FeatureItem(
            number = 3,
            title = "Route Tracking",
            desc = "Add photos to your routes to remember your journey"
        )
        FeatureItem(
            number = 4,
            title = "Route Tracking",
            desc = "Share routes and export data in multiple formats"
        )

        Spacer(Modifier.height(30.dp))


        // The Team
        Text(
            text = "The Team",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "TrackMyRoute was built by AdriAG, a passionate technology enthusiast and developer committed to making it easier for people to track, analyze, and share their journeys and routes.",
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(Modifier.height(20.dp))


        // Social media
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = { /* TODO: Follow on LinkedIn */ }) {
                Image(
                    painter = if (LocalIsDarkTheme.current) painterResource(id = R.drawable.logo_linkedin_wh) else painterResource(id = R.drawable.logo_linkedin_bl),
                    contentDescription = "LinkedIn",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Follow me",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            OutlinedButton(onClick = { /* TODO: Contact me */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = "LinkedIn",
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Reach me",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))
    }
}


@Composable
fun FeatureItem(
    number: Int,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background( colorResource(R.color.grayCircle), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = colorResource(R.color.black)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title
            )

            Text(
                text = desc,
                color = Color.Gray
            )
        }
    }
}



@Composable
@Preview
fun test2(){
    AboutUsScreen()
}