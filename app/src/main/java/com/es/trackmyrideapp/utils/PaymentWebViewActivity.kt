package com.es.trackmyrideapp.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.es.trackmyrideapp.BuildConfig
import com.es.trackmyrideapp.ui.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentWebViewActivity : AppCompatActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar flecha de volver en el action bar nativo
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val toolbar = Toolbar(this).apply {
            title = "Payment"
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                finish()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        val webView = WebView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(toolbar, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(webView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        setContentView(layout)

        // Configuración básica
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Controlador de navegación
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Detecta redirección de éxito del pago
                if (url != null && url.contains("payment-success")) {
                    sessionViewModel.activatePremiumUser()

                    finish()
                    return true
                }
                return false
            }
        }

        val paypalClientId = BuildConfig.PAYPAL_CLIENT_ID
        val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Checkout</title>
        <!-- PayPal SDK con client-id dinámico -->
        <script src="https://www.paypal.com/sdk/js?client-id=$paypalClientId"></script>
        <style>
            body {
                padding-top: 60px; 
                margin: 0;
            }
        </style>
    </head>
    <body>
        <div id="paypal-button-container"></div>

        <script>
            paypal.Buttons({
                createOrder: function(data, actions) {
                    return actions.order.create({
                        purchase_units: [{
                            amount: {
                                value: '14.99'
                            }
                        }]
                    });
                },
                onApprove: function(data, actions) {
                    return actions.order.capture().then(function(details) {
                        // Redirección falsa para simular éxito
                        window.location.href = "https://yourapp.com/payment-success";
                    });
                }
            }).render('#paypal-button-container');
        </script>
    </body>
    </html>
""".trimIndent()

        webView.loadDataWithBaseURL("https://www.paypal.com", htmlContent, "text/html", "UTF-8", null)
    }
}