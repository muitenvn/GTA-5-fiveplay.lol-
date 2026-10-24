package com.example.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bridge.WebGameBridge
import com.example.model.ControllerSettings
import com.example.model.GamepadMode
import com.example.ui.components.VirtualKeyboardOverlay
import kotlin.math.roundToInt

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FivePlayMainScreen(
    modifier: Modifier = Modifier
) {
    var controllerSettings by remember { mutableStateOf(ControllerSettings()) }
    var showControls by remember { mutableStateOf(true) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Draggable Floating Gamer HUD Widget Position
    var hudOffsetX by remember { mutableStateOf(80f) }
    var hudOffsetY by remember { mutableStateOf(20f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. FULLSCREEN LANDSCAPE WEBVIEW (fiveplay.lol)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val ws = this.settings
                    ws.javaScriptEnabled = true
                    ws.domStorageEnabled = true
                    ws.loadsImagesAutomatically = true
                    ws.useWideViewPort = true
                    ws.loadWithOverviewMode = true
                    ws.setSupportZoom(true)
                    ws.builtInZoomControls = true
                    ws.displayZoomControls = false
                    ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    ws.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            WebGameBridge.injectFullscreenStyling(view)
                        }
                    }

                    webChromeClient = WebChromeClient()
                    loadUrl("https://fiveplay.lol")
                    webViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. VIRTUAL PC KEYBOARD & MOUSE OVERLAY
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            VirtualKeyboardOverlay(
                settings = controllerSettings,
                onKey = { key, code, keyCode, isDown ->
                    WebGameBridge.sendKey(webViewRef, key, code, keyCode, isDown)
                },
                onMouseClick = { button, isDown ->
                    WebGameBridge.sendMouseClick(webViewRef, button, isDown)
                },
                onMouseMove = { dx, dy ->
                    WebGameBridge.sendMouseMove(
                        webViewRef,
                        dx * controllerSettings.mouseSensitivity,
                        dy * controllerSettings.mouseSensitivity
                    )
                }
            )
        }

        // 3. DRAGGABLE FLOATING GAMER HUD PILL
        Box(
            modifier = Modifier
                .offset { IntOffset(hudOffsetX.roundToInt(), hudOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        hudOffsetX += dragAmount.x
                        hudOffsetY += dragAmount.y
                    }
                }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.90f),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f)),
                modifier = Modifier.testTag("floating_hud_capsule")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mode Indicator / Switcher Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (controllerSettings.mode) {
                            GamepadMode.GTA_V_COMBAT -> Color(0xFF0284C7)
                            GamepadMode.GTA_V_DRIVING -> Color(0xFF059669)
                            GamepadMode.COMPACT -> Color(0xFF7C3AED)
                        },
                        onClick = {
                            val nextMode = when (controllerSettings.mode) {
                                GamepadMode.GTA_V_COMBAT -> GamepadMode.GTA_V_DRIVING
                                GamepadMode.GTA_V_DRIVING -> GamepadMode.COMPACT
                                GamepadMode.COMPACT -> GamepadMode.GTA_V_COMBAT
                            }
                            controllerSettings = controllerSettings.copy(mode = nextMode)
                        }
                    ) {
                        Text(
                            text = when (controllerSettings.mode) {
                                GamepadMode.GTA_V_COMBAT -> "🔫 Chiến đấu"
                                GamepadMode.GTA_V_DRIVING -> "🚗 Lái xe"
                                GamepadMode.COMPACT -> "⚡ Thu gọn"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Toggle Visibility Button
                    IconButton(
                        onClick = { showControls = !showControls },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (showControls) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle controls",
                            tint = if (showControls) Color(0xFF00E5FF) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Refresh Webview Button
                    IconButton(
                        onClick = {
                            webViewRef?.reload()
                            WebGameBridge.injectFullscreenStyling(webViewRef)
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = { showSettingsModal = true },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFFFD740),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 4. GAMER SETTINGS MODAL DIALOG
        if (showSettingsModal) {
            AlertDialog(
                onDismissRequest = { showSettingsModal = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Bảng điều khiển FivePlay", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Layout Mode Selector
                        Text("Chế độ điều khiển:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = controllerSettings.mode == GamepadMode.GTA_V_COMBAT,
                                onClick = { controllerSettings = controllerSettings.copy(mode = GamepadMode.GTA_V_COMBAT) },
                                label = { Text("Chiến đấu", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = controllerSettings.mode == GamepadMode.GTA_V_DRIVING,
                                onClick = { controllerSettings = controllerSettings.copy(mode = GamepadMode.GTA_V_DRIVING) },
                                label = { Text("Lái xe", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = controllerSettings.mode == GamepadMode.COMPACT,
                                onClick = { controllerSettings = controllerSettings.copy(mode = GamepadMode.COMPACT) },
                                label = { Text("Gọn", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Opacity
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Độ trong suốt bàn phím", fontSize = 13.sp)
                                Text("${(controllerSettings.opacity * 100).toInt()}%", fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = controllerSettings.opacity,
                                onValueChange = { controllerSettings = controllerSettings.copy(opacity = it) },
                                valueRange = 0.2f..1.0f
                            )
                        }

                        // Button Size Scale
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Kích thước nút bấm", fontSize = 13.sp)
                                Text(String.format("%.1fx", controllerSettings.buttonScale), fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = controllerSettings.buttonScale,
                                onValueChange = { controllerSettings = controllerSettings.copy(buttonScale = it) },
                                valueRange = 0.8f..1.3f
                            )
                        }

                        // Haptic Vibration Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rung phản hồi khi nhấn phím", fontSize = 13.sp)
                            Switch(
                                checked = controllerSettings.hapticFeedback,
                                onCheckedChange = { controllerSettings = controllerSettings.copy(hapticFeedback = it) }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showSettingsModal = false }) {
                        Text("Áp dụng")
                    }
                }
            )
        }
    }
}
