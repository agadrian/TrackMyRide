package com.es.trackmyrideapp.ui.screens.payment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.es.trackmyrideapp.BuildConfig
import com.es.trackmyrideapp.data.local.AuthPreferences
import com.es.trackmyrideapp.ui.viewmodels.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentWebViewActivity : AppCompatActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()
    private lateinit var authPreferences: AuthPreferences

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar preferencias para obtener token JWT
        authPreferences = AuthPreferences(this)

        // Layout principal vertical con gradiente de fondo y padding inferior
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#f8fafc"), Color.parseColor("#e2e8f0"))
            )
            background = gradientDrawable

            val paddingInDp = (24 * resources.displayMetrics.density).toInt()
            setPadding(0, 0, 0, paddingInDp)
        }

        // Toolbar
        val toolbar = Toolbar(this).apply {
            title = "Secure Payment"
            setTitleTextColor(Color.parseColor("#1f2937"))
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener { finish() }
            navigationIcon?.setTint(Color.BLACK)
            setBackgroundColor(Color.WHITE)
            elevation = 4f
        }

        // Ajustar padding superior para evitar solaparse con la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        // Contenedor superior para título + subtítulo
        val topContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (32 * resources.displayMetrics.density).toInt()
                bottomMargin = (8 * resources.displayMetrics.density).toInt()
            }
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Título grande en la pantalla
        val headerTextView = TextView(this).apply {
            text = "Almost there!"
            textSize = 26f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#1f2937"))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
        }

        // Subtítulo explicativo
        val subtitleTextView = TextView(this).apply {
            text = "Complete your secure payment to unlock premium features"
            textSize = 16f
            setTextColor(Color.parseColor("#6b7280"))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(24, 0, 24, 0)
        }

        // Añadir título y subtítulo al contenedor
        topContainer.addView(headerTextView)
        topContainer.addView(subtitleTextView)

        // WebView que ocupará el espacio restante para mostrar el pago PayPal
        val webView = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // Ocupa el resto de la pantalla
            ).apply {
                topMargin = (12 * resources.displayMetrics.density).toInt()
                leftMargin = (16 * resources.displayMetrics.density).toInt()
                rightMargin = (16 * resources.displayMetrics.density).toInt()
            }
            setBackgroundColor(Color.TRANSPARENT)
            isDrawingCacheEnabled = true
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        // Añadir toolbar, contenedor de textos y WebView al layout principal
        mainLayout.addView(toolbar)
        mainLayout.addView(topContainer)
        mainLayout.addView(webView)

        setContentView(mainLayout)

        // Configuración del WebView para permitir JS y manejo correcto de la web
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        // Interceptar URLs cargadas para detectar éxito de pago
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Detectar URL que indica pago exitoso
                if (url != null && url.contains("payment-success")) {
                    val token = authPreferences.getJwtToken()
                    if (!token.isNullOrBlank()) {
                        // Activar estado premium en ViewModel con token JWT
                        sessionViewModel.activatePremiumUser(token)

                        // Esperar resultado de activación premium y cerrar pantalla
                        lifecycleScope.launch {
                            sessionViewModel.premiumActivated.collect { activated ->
                                if (activated) {
                                    sessionViewModel.resetPremiumActivationFlag()
                                    finish()
                                }
                            }
                        }
                    } else {
                        finish()
                    }
                    return true
                }
                return false
            }
        }


        val paypalClientId = BuildConfig.PAYPAL_CLIENT_ID

        // HTML embebido con PayPal SDK para realizar el pago
        val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PayPal Checkout</title>
    <script src="https://www.paypal.com/sdk/js?client-id=$paypalClientId"></script>
    <style>
        html, body {
            background-color: transparent;
            color: #000;
            margin: 0;
            padding: 0 20px 40px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            min-height: 350px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        #paypal-button-container {
            width: 100%;
            max-width: 400px;
            margin: 0 auto;
        }
    </style>
    </head>
        <body>
            <div id="paypal-button-container"></div>
        
            <script>
                paypal.Buttons({
                    style: {
                        layout: 'vertical',
                        color: 'blue',
                        shape: 'pill',
                        label: 'pay',
                        height: 40,
                        tagline: false
                    },
                    createOrder: function(data, actions) {
                        return actions.order.create({
                            purchase_units: [{
                                amount: {
                                    value: '14.99'
                                },
                                description: 'Premium Access - Lifetime'
                            }]
                        });
                    },
                    onApprove: function(data, actions) {
                        return actions.order.capture().then(function(details) {
                            window.location.href = "https://yourapp.com/payment-success";
                        });
                    },
                    onError: function(err) {
                        console.error('PayPal error:', err);
                        alert('Ha ocurrido un error con el pago. Por favor, inténtalo de nuevo.');
                    },
                    onCancel: function(data) {
                        console.log('Payment cancelled');
                    }
                }).render('#paypal-button-container');
            </script>
        </body>
    </html>
""".trimIndent()

        webView.loadDataWithBaseURL("https://www.paypal.com", htmlContent, "text/html", "UTF-8", null)
    }
}