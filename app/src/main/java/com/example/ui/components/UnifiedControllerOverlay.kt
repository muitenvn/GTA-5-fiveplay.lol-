package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControllerSettings
import com.example.model.ControlType
import com.example.model.VirtualButtonData
import kotlin.math.roundToInt

@Composable
fun UnifiedControllerOverlay(
    buttons: List<VirtualButtonData>,
    controlType: ControlType,
    isEditMode: Boolean,
    settings: ControllerSettings,
    onButtonMoved: (buttonId: String, newXPercent: Float, newYPercent: Float) -> Unit,
    onButtonSelectEdit: (VirtualButtonData) -> Unit,
    onKey: (key: String, code: String, keyCode: Int, isDown: Boolean) -> Unit,
    onMouseClick: (button: Int, isDown: Boolean) -> Unit,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .alpha(settings.opacity)
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // 1. Render all dynamic buttons (WASD, Action, Custom, Headlights H, etc.)
        buttons.forEach { btn ->
            val initialX = btn.xPercent * screenWidthPx
            val initialY = btn.yPercent * screenHeightPx

            var currentX by remember(btn.id, btn.xPercent, screenWidthPx) { mutableFloatStateOf(initialX) }
            var currentY by remember(btn.id, btn.yPercent, screenHeightPx) { mutableFloatStateOf(initialY) }

            val buttonColor = try {
                Color(android.graphics.Color.parseColor(btn.colorHex))
            } catch (e: Exception) {
                Color(0xFF1E293B)
            }
            val activeColor = try {
                Color(android.graphics.Color.parseColor(btn.activeColorHex))
            } catch (e: Exception) {
                Color(0xFF00E5FF)
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                    .size((btn.widthDp * settings.buttonScale).dp, (btn.heightDp * settings.buttonScale).dp)
                    .then(
                        if (isEditMode) {
                            Modifier
                                .pointerInput(btn.id) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            val newXp = (currentX / screenWidthPx).coerceIn(0.01f, 0.95f)
                                            val newYp = (currentY / screenHeightPx).coerceIn(0.01f, 0.95f)
                                            onButtonMoved(btn.id, newXp, newYp)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            currentX = (currentX + dragAmount.x).coerceIn(0f, screenWidthPx - 50f)
                                            currentY = (currentY + dragAmount.y).coerceIn(0f, screenHeightPx - 50f)
                                        }
                                    )
                                }
                                .border(1.5.dp, Color(0xFFFBBF24), RoundedCornerShape(10.dp))
                                .clickable { onButtonSelectEdit(btn) }
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (isEditMode) {
                    // Edit Mode visual representation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = buttonColor.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(btn.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (btn.subLabel != null) {
                                    Text(btn.subLabel, color = Color(0xFFFBBF24), fontSize = 8.sp, maxLines = 1)
                                }
                            }
                            Icon(
                                Icons.Default.OpenWith,
                                contentDescription = "Move",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp)
                            )
                        }
                    }
                } else {
                    // Gaming Mode with real touch down/up
                    GamerTouchKey(
                        label = btn.label,
                        subLabel = btn.subLabel,
                        baseColor = buttonColor,
                        activeColor = activeColor,
                        hapticEnabled = settings.hapticFeedback,
                        modifier = Modifier.fillMaxSize()
                    ) { isDown ->
                        onKey(btn.key, btn.code, btn.keyCode, isDown)
                    }
                }
            }
        }

        // 2. Extra Static Controls for Keyboard Mode (Camera Touchpad & Aim/Shoot)
        if (controlType == ControlType.KEYBOARD && !isEditMode) {
            // Camera Touchpad (Bottom-Right area above buttons)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 70.dp, end = 16.dp)
                    .width(180.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.40f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onMouseMove(dragAmount.x, dragAmount.y)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡ Vuốt xoay camera",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Aim (R-Click) & Shoot (L-Click) fixed bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GamerTouchKey(
                    label = "🎯 NGẮM",
                    subLabel = "R-Click",
                    baseColor = Color(0xFF1E3A8A),
                    activeColor = Color(0xFF3B82F6),
                    hapticEnabled = settings.hapticFeedback,
                    modifier = Modifier.width(90.dp).height(46.dp)
                ) { isDown ->
                    onMouseClick(2, isDown)
                }

                GamerTouchKey(
                    label = "🔥 BẮN",
                    subLabel = "L-Click",
                    baseColor = Color(0xFF991B1B),
                    activeColor = Color(0xFFEF4444),
                    hapticEnabled = settings.hapticFeedback,
                    modifier = Modifier.width(90.dp).height(46.dp)
                ) { isDown ->
                    onMouseClick(0, isDown)
                }
            }
        }
    }
}
