package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControllerSettings

@Composable
fun VirtualGamepadOverlay(
    settings: ControllerSettings,
    onButtonPressed: (String) -> Unit,
    onJoystickMoved: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val alpha = settings.opacity

    fun triggerHaptic() {
        if (settings.hapticFeedback) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(35)
                }
            } catch (e: Exception) {
                // Ignore if vibration fails
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
            .padding(12.dp)
    ) {
        // Top Shoulder & Trigger Buttons (L1/L2 and R1/R2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GamepadButton("L2", Modifier.width(60.dp).height(36.dp)) {
                    triggerHaptic()
                    onButtonPressed("L2")
                }
                GamepadButton("L1", Modifier.width(60.dp).height(36.dp)) {
                    triggerHaptic()
                    onButtonPressed("L1")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GamepadButton("R1", Modifier.width(60.dp).height(36.dp)) {
                    triggerHaptic()
                    onButtonPressed("R1")
                }
                GamepadButton("R2", Modifier.width(60.dp).height(36.dp)) {
                    triggerHaptic()
                    onButtonPressed("R2")
                }
            }
        }

        // GTA V Shortcut Bar (F, Shift, Space, Tab, M, ESC, etc.)
        if (settings.showShortcutsBar) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 44.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShortcutKey("F (Car)", "F") { triggerHaptic(); onButtonPressed("F") }
                    ShortcutKey("Shift", "SHIFT") { triggerHaptic(); onButtonPressed("SHIFT") }
                    ShortcutKey("Space", "SPACE") { triggerHaptic(); onButtonPressed("SPACE") }
                    ShortcutKey("Tab", "TAB") { triggerHaptic(); onButtonPressed("TAB") }
                    ShortcutKey("M (Menu)", "M") { triggerHaptic(); onButtonPressed("M") }
                    ShortcutKey("ESC", "ESC") { triggerHaptic(); onButtonPressed("ESC") }
                    ShortcutKey("R", "RELOAD") { triggerHaptic(); onButtonPressed("R") }
                    ShortcutKey("Horn", "HORN") { triggerHaptic(); onButtonPressed("HORN") }
                }
            }
        }

        // Left Side: D-Pad & Left Analog Joystick
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 24.dp, start = 16.dp)
                .size(170.dp)
        ) {
            VirtualJoystick(
                label = "MOVE",
                modifier = Modifier.align(Alignment.Center),
                onMove = { dx, dy -> onJoystickMoved("LEFT_STICK", dx, dy) }
            )
        }

        // Right Side: ABXY Action Buttons & Right Analog Joystick (Camera)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
                .size(190.dp)
        ) {
            // Right Analog Joystick (Camera / Aim)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(85.dp)
            ) {
                VirtualJoystick(
                    label = "CAM",
                    modifier = Modifier.fillMaxSize(),
                    onMove = { dx, dy -> onJoystickMoved("RIGHT_STICK", dx, dy) }
                )
            }

            // ABXY Buttons Layout
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(110.dp)
            ) {
                // Y Button (Triangle) - Top
                GamepadActionButton("Y", Color(0xFF00E676), Modifier.align(Alignment.TopCenter)) {
                    triggerHaptic()
                    onButtonPressed("Y")
                }
                // X Button (Square) - Left
                GamepadActionButton("X", Color(0xFF2979FF), Modifier.align(Alignment.CenterStart)) {
                    triggerHaptic()
                    onButtonPressed("X")
                }
                // B Button (Circle) - Right
                GamepadActionButton("B", Color(0xFFFF1744), Modifier.align(Alignment.CenterEnd)) {
                    triggerHaptic()
                    onButtonPressed("B")
                }
                // A Button (Cross) - Bottom
                GamepadActionButton("A", Color(0xFFFFEA00), Modifier.align(Alignment.BottomCenter)) {
                    triggerHaptic()
                    onButtonPressed("A")
                }
            }
        }
    }
}

@Composable
fun GamepadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.testTag("btn_$text"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(2.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun ShortcutKey(
    label: String,
    action: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.height(32.dp).testTag("shortcut_$action"),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GamepadActionButton(
    text: String,
    buttonColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(buttonColor.copy(alpha = 0.85f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun VirtualJoystick(
    label: String,
    modifier: Modifier = Modifier,
    onMove: (Float, Float) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val maxRadius = 40f

    Box(
        modifier = modifier
            .size(85.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                        onMove(0f, 0f)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(-maxRadius, maxRadius)
                        offsetY = (offsetY + dragAmount.y).coerceIn(-maxRadius, maxRadius)
                        onMove(offsetX / maxRadius, offsetY / maxRadius)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer ring indicator
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )
        // Movable thumb stick
        Box(
            modifier = Modifier
                .size(36.dp)
                .offset(x = offsetX.dp, y = offsetY.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, color = MaterialTheme.colorScheme.onPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
