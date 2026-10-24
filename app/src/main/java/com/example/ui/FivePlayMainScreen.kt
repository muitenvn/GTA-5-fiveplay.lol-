package com.example.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.ControllerSettings
import com.example.ui.components.VirtualKeyboardOverlay
import kotlin.math.roundToInt

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FivePlayMainScreen(
    modifier: Modifier = Modifier
) {
    var controllerSettings by remember { mutableStateOf(ControllerSettings()) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showKeyboardOverlay by remember { mutableStateOf(true) }

    // Floating button coordinates (draggable anywhere on landscape screen)
    var offsetX by remember { mutableStateOf(100f) }
    var offsetY by remember { mutableStateOf(40f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fullscreen WebView loading fiveplay.lol optimized for landscape PC view
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
                    // Force desktop user agent so fiveplay.lol renders full PC GTA V cloud streaming interface
                    webSettings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl("https://fiveplay.lol")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Virtual Keyboard Overlay (WASD, Mouse, PC shortcuts)
        if (showKeyboardOverlay) {
            VirtualKeyboardOverlay(
                settings = controllerSettings,
                onKeyPressed = { key -> },
                onMouseMoved = { dx, dy -> }
            )
        }

        // Floating Draggable Horizontal Pill Button
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                onClick = { showSettingsDialog = true },
                modifier = Modifier.testTag("floating_settings_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Cài đặt bàn phím",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Customization Settings Dialog
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("Tùy chỉnh bàn phím ảo PC") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Toggle Keyboard
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hiển thị bàn phím ảo", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = showKeyboardOverlay,
                                onCheckedChange = { showKeyboardOverlay = it }
                            )
                        }

                        // Opacity
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Độ trong suốt", fontWeight = FontWeight.Medium)
                                Text("${(controllerSettings.opacity * 100).toInt()}%")
                            }
                            Slider(
                                value = controllerSettings.opacity,
                                onValueChange = { controllerSettings = controllerSettings.copy(opacity = it) },
                                valueRange = 0.3f..1.0f
                            )
                        }

                        // Haptic Vibration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rung khi bấm (Haptic)", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = controllerSettings.hapticFeedback,
                                onCheckedChange = { controllerSettings = controllerSettings.copy(hapticFeedback = it) }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showSettingsDialog = false }) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
}
