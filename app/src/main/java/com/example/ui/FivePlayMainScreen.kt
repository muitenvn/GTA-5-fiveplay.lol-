package com.example.ui

import com.example.MainActivity
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bridge.WebGameBridge
import com.example.model.ControllerSettings
import com.example.model.ControlType
import com.example.model.LayoutProfile
import com.example.model.VirtualButtonData
import com.example.storage.LayoutRepository
import com.example.ui.components.AddButtonDialog
import com.example.ui.components.EditButtonDialog
import com.example.ui.components.UnifiedControllerOverlay
import kotlin.math.roundToInt

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FivePlayMainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    var controllerSettings by remember { mutableStateOf(ControllerSettings()) }
    var controlType by remember { mutableStateOf(ControlType.KEYBOARD) }
    var layoutProfile by remember { mutableStateOf(LayoutProfile.DEFAULT) }
    var isEditMode by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEditButton by remember { mutableStateOf<VirtualButtonData?>(null) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Buttons list loaded from LayoutRepository
    var buttonsList by remember(controlType, layoutProfile) {
        mutableStateOf(LayoutRepository.loadButtons(context, controlType, layoutProfile))
    }

    // Draggable Floating HUD Position (default at top edge)
    var hudOffsetX by remember { mutableFloatStateOf(40f) }
    var hudOffsetY by remember { mutableFloatStateOf(12f) }

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

        // 2. UNIFIED VIRTUAL CONTROLLER OVERLAY (All Virtual Buttons)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            UnifiedControllerOverlay(
                buttons = buttonsList,
                controlType = controlType,
                isEditMode = isEditMode,
                settings = controllerSettings,
                onButtonMoved = { id, newX, newY ->
                    buttonsList = buttonsList.map {
                        if (it.id == id) it.copy(xPercent = newX, yPercent = newY) else it
                    }.toMutableList()
                    if (layoutProfile == LayoutProfile.DEFAULT) {
                        layoutProfile = LayoutProfile.CUSTOM
                    }
                },
                onButtonSelectEdit = { btn ->
                    selectedEditButton = btn
                },
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

        // 3. EDIT MODE TOP BANNER & ACTION BAR
        if (isEditMode) {
            Surface(
                color = Color(0xFF0F172A).copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24)),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                        Text(
                            text = "CHẾ ĐỘ TÙY CHỈNH: Kéo phím để dời | Chạm để đổi cỡ/xóa",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm nút ảo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                LayoutRepository.saveButtons(context, controlType, buttonsList)
                                Toast.makeText(context, "Đã lưu vị trí & kích thước phím!", Toast.LENGTH_SHORT).show()
                                isEditMode = false
                                activity?.hideSystemUI()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lưu & Thoát", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                buttonsList = LayoutRepository.resetToDefault(context, controlType)
                                layoutProfile = LayoutProfile.DEFAULT
                                Toast.makeText(context, "Đã đặt lại bố cục mặc định!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Đặt lại", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4. FLOATING HUD TOOLBAR (Thu gọn nút ảo & Mở rộng nút ảo)
        if (!isEditMode) {
            var dragDistance by remember { mutableFloatStateOf(0f) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(hudOffsetX.roundToInt(), hudOffsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { dragDistance = 0f },
                            onDrag = { change, dragAmount ->
                                dragDistance += Math.abs(dragAmount.x) + Math.abs(dragAmount.y)
                                if (dragDistance >= 12f) {
                                    change.consume()
                                    hudOffsetX += dragAmount.x
                                    hudOffsetY += dragAmount.y
                                }
                            }
                        )
                    }
            ) {
                if (!showControls) {
                    // WHEN VIRTUAL BUTTONS ARE COLLAPSED: A clear, comfortable, handsome floating button!
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.90f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                        shadowElevation = 10.dp,
                        onClick = {
                            if (dragDistance < 12f) {
                                showControls = true
                                activity?.hideSystemUI()
                            }
                        },
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("expand_controls_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (controlType == ControlType.KEYBOARD) Icons.Default.Keyboard else Icons.Default.Gamepad,
                                contentDescription = "Mở rộng nút ảo",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Mở rộng nút ảo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    // WHEN VIRTUAL BUTTONS ARE EXPANDED: Full toolbar with clear "Thu gọn nút ảo" button!
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.92f),
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
                            // MAIN BUTTON: THU GỌN NÚT ẢO
                            FilledTonalButton(
                                onClick = {
                                    if (dragDistance < 12f) {
                                        showControls = false
                                        activity?.hideSystemUI()
                                    }
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF1E293B),
                                    contentColor = Color(0xFF38BDF8)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Thu gọn",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Thu gọn nút ảo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Switch between Keyboard and Gamepad
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (controlType == ControlType.KEYBOARD) Color(0xFF0284C7) else Color(0xFF7C3AED),
                                onClick = {
                                    if (dragDistance < 12f) {
                                        controlType = if (controlType == ControlType.KEYBOARD) ControlType.GAMEPAD else ControlType.KEYBOARD
                                        buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                    }
                                }
                            ) {
                                Text(
                                    text = if (controlType == ControlType.KEYBOARD) "⌨️ Phím" else "🎮 Pad",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }

                            // Switch between Default and Custom Profile
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (layoutProfile == LayoutProfile.DEFAULT) Color(0xFF059669) else Color(0xFFD97706),
                                onClick = {
                                    if (dragDistance < 12f) {
                                        layoutProfile = if (layoutProfile == LayoutProfile.DEFAULT) LayoutProfile.CUSTOM else LayoutProfile.DEFAULT
                                        buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                    }
                                }
                            ) {
                                Text(
                                    text = if (layoutProfile == LayoutProfile.DEFAULT) "Mặc định" else "Tùy chỉnh",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }

                            // Edit Layout Button
                            FilledTonalButton(
                                onClick = {
                                    if (dragDistance < 12f) {
                                        isEditMode = true
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Chỉnh phím", fontSize = 11.sp)
                            }

                            // Refresh WebView
                            IconButton(
                                onClick = {
                                    if (dragDistance < 12f) {
                                        webViewRef?.reload()
                                        WebGameBridge.injectFullscreenStyling(webViewRef)
                                    }
                                },
                                modifier = Modifier.size(28.dp)
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
                                onClick = {
                                    if (dragDistance < 12f) {
                                        showSettingsModal = true
                                    }
                                },
                                modifier = Modifier.size(28.dp)
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
            }
        }

        // 5. DIALOG: ADD VIRTUAL BUTTON (Full PC Keyboard Selector)
        if (showAddDialog) {
            AddButtonDialog(
                onDismiss = {
                    showAddDialog = false
                    activity?.hideSystemUI()
                },
                onAddButton = { newBtn ->
                    buttonsList = (buttonsList + newBtn).toMutableList()
                    layoutProfile = LayoutProfile.CUSTOM
                    LayoutRepository.saveButtons(context, controlType, buttonsList)
                    Toast.makeText(context, "Đã thêm phím: ${newBtn.label}!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 6. DIALOG: EDIT SELECTED BUTTON
        selectedEditButton?.let { btn ->
            EditButtonDialog(
                button = btn,
                onDismiss = {
                    selectedEditButton = null
                    activity?.hideSystemUI()
                },
                onUpdateSize = { w, h ->
                    buttonsList = buttonsList.map {
                        if (it.id == btn.id) it.copy(widthDp = w, heightDp = h) else it
                    }.toMutableList()
                    layoutProfile = LayoutProfile.CUSTOM
                },
                onDelete = {
                    buttonsList = buttonsList.filter { it.id != btn.id }.toMutableList()
                    layoutProfile = LayoutProfile.CUSTOM
                    selectedEditButton = null
                }
            )
        }

        // 7. GAMER SETTINGS MODAL DIALOG
        if (showSettingsModal) {
            AlertDialog(
                onDismissRequest = {
                    showSettingsModal = false
                    activity?.hideSystemUI()
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Cài đặt điều khiển", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Control Type Selector
                        Text("Loại điều khiển:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = controlType == ControlType.KEYBOARD,
                                onClick = {
                                    controlType = ControlType.KEYBOARD
                                    buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                },
                                label = { Text("⌨️ Bàn phím PC") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = controlType == ControlType.GAMEPAD,
                                onClick = {
                                    controlType = ControlType.GAMEPAD
                                    buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                },
                                label = { Text("🎮 Tay cầm") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Profile Selector
                        Text("Bố cục:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = layoutProfile == LayoutProfile.DEFAULT,
                                onClick = {
                                    layoutProfile = LayoutProfile.DEFAULT
                                    buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                },
                                label = { Text("Mặc định") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = layoutProfile == LayoutProfile.CUSTOM,
                                onClick = {
                                    layoutProfile = LayoutProfile.CUSTOM
                                    buttonsList = LayoutRepository.loadButtons(context, controlType, layoutProfile)
                                },
                                label = { Text("Tùy chỉnh") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Opacity
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Độ trong suốt", fontSize = 13.sp)
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
                                Text("Tỉ lệ nút bấm", fontSize = 13.sp)
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
                            Text("Rung phản hồi", fontSize = 13.sp)
                            Switch(
                                checked = controllerSettings.hapticFeedback,
                                onCheckedChange = { controllerSettings = controllerSettings.copy(hapticFeedback = it) }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showSettingsModal = false
                        activity?.hideSystemUI()
                    }) {
                        Text("Áp dụng")
                    }
                }
            )
        }
    }
}
