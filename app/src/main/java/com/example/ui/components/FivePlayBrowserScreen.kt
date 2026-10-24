package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.ControllerSettings

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FivePlayBrowserScreen(
    settings: ControllerSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var url by remember { mutableStateOf("https://fiveplay.lol") }
    var currentUrl by remember { mutableStateOf("https://fiveplay.lol") }
    var pageTitle by remember { mutableStateOf("FivePlay - GTA 5 Cloud Gaming") }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0) }
    var showGamepadOverlay by remember { mutableStateOf(true) }
    var isDesktopMode by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        modifier = Modifier.size(36.dp).testTag("browser_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        modifier = Modifier.size(36.dp).testTag("browser_forward")
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                    }
                    IconButton(
                        onClick = { webViewRef?.reload() },
                        modifier = Modifier.size(36.dp).testTag("browser_refresh")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    // URL / Status Bar
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentUrl,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }

                    // Toggle Gamepad Overlay Button
                    FilledTonalButton(
                        onClick = { showGamepadOverlay = !showGamepadOverlay },
                        modifier = Modifier.height(36.dp).testTag("toggle_gamepad_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (showGamepadOverlay) Icons.Default.Gamepad else Icons.Default.VideogameAssetOff,
                            contentDescription = "Toggle Pad",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (showGamepadOverlay) "Pad ON" else "Pad OFF", fontSize = 11.sp)
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                    )
                }
            }
        }

        // WebView and Gamepad Overlay Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        val webSettings = this.settings
                        webSettings.javaScriptEnabled = true
                        webSettings.domStorageEnabled = true
                        webSettings.loadsImagesAutomatically = true
                        webSettings.useWideViewPort = true
                        webSettings.loadWithOverviewMode = true
                        webSettings.setSupportZoom(true)
                        webSettings.builtInZoomControls = true
                        webSettings.displayZoomControls = false
                        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        webSettings.userAgentString = if (isDesktopMode) {
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                        } else {
                            webSettings.userAgentString
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { currentUrl = it }
                                isLoading = false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                                isLoading = newProgress < 100
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { pageTitle = it }
                            }
                        }

                        loadUrl(url)
                        webViewRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Floating Virtual Gamepad Overlay on top of WebView
            if (showGamepadOverlay) {
                VirtualGamepadOverlay(
                    settings = settings,
                    onButtonPressed = { action ->
                        // Inject simulated key events or interaction if needed
                    },
                    onJoystickMoved = { stick, dx, dy ->
                        // Handle joystick telemetry
                    }
                )
            }
        }
    }
}
