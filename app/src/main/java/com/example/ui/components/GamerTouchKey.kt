package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GamerTouchKey(
    label: String,
    subLabel: String? = null,
    baseColor: Color = Color(0xFF1E2638),
    activeColor: Color = Color(0xFF00E5FF),
    textColor: Color = Color.White,
    hapticEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    onTouch: (isDown: Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun vibrate() {
        if (!hapticEnabled) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                mgr.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .shadow(if (isPressed) 8.dp else 2.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPressed) activeColor.copy(alpha = 0.85f) else baseColor.copy(alpha = 0.65f)
            )
            .border(
                width = if (isPressed) 2.dp else 1.dp,
                color = if (isPressed) Color.White else activeColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> {
                                if (!isPressed) {
                                    isPressed = true
                                    vibrate()
                                    onTouch(true)
                                }
                            }
                            PointerEventType.Release -> {
                                if (isPressed) {
                                    isPressed = false
                                    onTouch(false)
                                }
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = label,
                color = if (isPressed) Color.Black else textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = if (isPressed) Color.Black.copy(alpha = 0.8f) else textColor.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
