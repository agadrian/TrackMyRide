package com.es.trackmyrideapp.ui.screens.premiumScreen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.utils.PaymentWebViewActivity


@Composable
fun PremiumScreen(
    modifier: Modifier = Modifier
){

    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            Modifier
                .padding(horizontal = 30.dp)
                .padding(top = 16.dp)
                .navigationBarsPadding()
        ) {
            // PREMIUM Tag
            Box(
                modifier = Modifier
                    .background(colorResource(R.color.orangeBackground), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "PREMIUM",
                    color = colorResource(R.color.brownFontPremium),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(15.dp))

            Text(
                text = "For those who want to take it to the next level!",
                fontSize = 15.sp,
            )

            Spacer(Modifier.height(30.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(30.dp))



            Text(
                text = "$14.99",
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold,
                color = if (LocalIsDarkTheme.current) colorResource(R.color.brownFontPremiumDark) else colorResource(R.color.brownFontPremium)
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Lifetime access",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(30.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(30.dp))


            // Features List
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumFeature("Share routes with friends")
                PremiumFeature("Export routes")
                PremiumFeature("Extra images")
                PremiumFeature("Full routes history")
                PremiumFeature("3D View", "Coming Soon")
                PremiumFeature("Weather Info", "Coming Soon")
            }

            Spacer(modifier = Modifier.weight(1f))


            CustomButton(
                onclick = {
                    val intent = Intent(context, PaymentWebViewActivity::class.java)
                    context.startActivity(intent)
                },
                buttonColor = colorResource(R.color.orangeButton),
                fontColor = colorResource(R.color.black),
                text = "Go Premium"
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}




@Composable
fun PremiumFeature(
    text: String,
    badge: String? = null
) {
    Row(
        Modifier
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (badge != null) {
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(colorResource(R.color.orangeBackground), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = colorResource(R.color.brownFontPremium),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun cxvxcv(){
    PremiumScreen()
}