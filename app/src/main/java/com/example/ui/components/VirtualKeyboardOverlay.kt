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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControllerSettings

@Composable
fun VirtualKeyboardOverlay(
    settings: ControllerSettings,
    onKeyPressed: (String) -> Unit,
    onMouseMoved: (Float, Float) -> Unit,
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
                    vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(30)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
            .padding(12.dp)
    ) {
        // Top Bar: Weapon numbers (1-5) and Menu keys (ESC, M, Tab)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PCKeyButton("ESC") { triggerHaptic(); onKeyPressed("ESC") }
            PCKeyButton("TAB") { triggerHaptic(); onKeyPressed("TAB") }
            PCKeyButton("M") { triggerHaptic(); onKeyPressed("M") }
            Spacer(modifier = Modifier.width(16.dp))
            PCKeyButton("1") { triggerHaptic(); onKeyPressed("1") }
            PCKeyButton("2") { triggerHaptic(); onKeyPressed("2") }
            PCKeyButton("3") { triggerHaptic(); onKeyPressed("3") }
            PCKeyButton("4") { triggerHaptic(); onKeyPressed("4") }
            PCKeyButton("5") { triggerHaptic(); onKeyPressed("5") }
        }

        // Top-Right Quick Action Keys (R, G, F, E, Q)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PCKeyButton("Q") { triggerHaptic(); onKeyPressed("Q") }
            PCKeyButton("E") { triggerHaptic(); onKeyPressed("E") }
            PCKeyButton("F (Lên xe)") { triggerHaptic(); onKeyPressed("F") }
            PCKeyButton("R (Nạp đạn)") { triggerHaptic(); onKeyPressed("R") }
            PCKeyButton("G (Lựu đạn)") { triggerHaptic(); onKeyPressed("G") }
        }

        // Bottom-Left: WASD Cluster & Shift, Space, Ctrl
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp, start = 16.dp)
                .size(190.dp)
        ) {
            // WASD D-Pad layout
            Box(modifier = Modifier.size(130.dp).align(Alignment.TopStart)) {
                // W (Up)
                PCKeyButton("W", Modifier.align(Alignment.TopCenter).size(42.dp)) { triggerHaptic(); onKeyPressed("W") }
                // A (Left)
                PCKeyButton("A", Modifier.align(Alignment.CenterStart).size(42.dp)) { triggerHaptic(); onKeyPressed("A") }
                // S (Down)
                PCKeyButton("S", Modifier.align(Alignment.BottomCenter).size(42.dp)) { triggerHaptic(); onKeyPressed("S") }
                // D (Right)
                PCKeyButton("D", Modifier.align(Alignment.CenterEnd).size(42.dp)) { triggerHaptic(); onKeyPressed("D") }
            }

            // Modifier Keys (Shift, Space, Ctrl) at bottom
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PCKeyButton("CTRL", Modifier.width(50.dp).height(38.dp)) { triggerHaptic(); onKeyPressed("CTRL") }
                PCKeyButton("SHIFT", Modifier.width(60.dp).height(38.dp)) { triggerHaptic(); onKeyPressed("SHIFT") }
                PCKeyButton("SPACE", Modifier.width(75.dp).height(38.dp)) { triggerHaptic(); onKeyPressed("SPACE") }
            }
        }

        // Bottom-Right: Mouse Touchpad (for Camera look/aim) and Mouse Left/Right Click buttons
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // Touchpad area for looking around
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {},
                            onDragEnd = {},
                            onDragCancel = {},
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onMouseMoved(dragAmount.x, dragAmount.y)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vùng rê chuột / Góc nhìn",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mouse Click Buttons (Left Click / Right Click)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PCMouseButton("BẮT (L-Click)", Color(0xFF2979FF)) { triggerHaptic(); onKeyPressed("MOUSE_LEFT") }
                PCMouseButton("NGẮM (R-Click)", Color(0xFFFF1744)) { triggerHaptic(); onKeyPressed("MOUSE_RIGHT") }
            }
        }
    }
}

@Composable
fun PCKeyButton(
    text: String,
    modifier: Modifier = Modifier.size(44.dp),
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
        tonalElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PCMouseButton(
    text: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = buttonColor.copy(alpha = 0.85f),
        tonalElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
