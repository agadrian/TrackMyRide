package com.es.trackmyrideapp.ui.screens.premiumScreen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.es.trackmyrideapp.LocalIsDarkTheme
import com.es.trackmyrideapp.LocalSessionViewModel
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.screens.payment.PaymentWebViewActivity


@Composable
fun PremiumScreen(
    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    //  Llamo con launchedeffect a la api para comprobar el premium, y luego miro el estado obtenido
    val sessionViewModel = LocalSessionViewModel.current
    val isPremium by sessionViewModel.isPremium.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sessionViewModel.checkPremiumStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    if (!isPremium){

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
                Text(
                    text = "Compare Plans",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Free", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        FeatureRow("Upload Images", true)
                        FeatureRow("Route history", true)
                        FeatureRow("Select Layers", true)
                        FeatureRow("Export routes", false)
                        FeatureRow("Share routes", false)

                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Premium", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        FeatureRow("Extra images (+5)", true)
                        FeatureRow("Full routes history", true)
                        FeatureRow("Select Layers", true)
                        FeatureRow("Share routes", true)
                        FeatureRow("Export routes", true)
                    }
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
    }else{
        PremiumActiveScreen(modifier)
    }

}


@Composable
fun FeatureRow(feature: String, enabled: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Close,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = feature,
            color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}