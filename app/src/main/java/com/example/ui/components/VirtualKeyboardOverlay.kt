package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.example.model.GamepadMode

@Composable
fun VirtualKeyboardOverlay(
    settings: ControllerSettings,
    onKey: (key: String, code: String, keyCode: Int, isDown: Boolean) -> Unit,
    onMouseClick: (button: Int, isDown: Boolean) -> Unit,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = settings.opacity
    val haptic = settings.hapticFeedback
    val scale = settings.buttonScale

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // TOP BAR: Utility and Numbers
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GamerTouchKey("ESC", "Thoát", Color(0xFF37474F), Color(0xFFFF5252), hapticEnabled = haptic, modifier = Modifier.size((38 * scale).dp)) { isDown ->
                onKey("Escape", "Escape", 27, isDown)
            }
            GamerTouchKey("TAB", "Vũ khí", Color(0xFF37474F), Color(0xFF40C4FF), hapticEnabled = haptic, modifier = Modifier.size((38 * scale).dp)) { isDown ->
                onKey("Tab", "Tab", 9, isDown)
            }
            GamerTouchKey("M", "Menu", Color(0xFF37474F), Color(0xFFFFD740), hapticEnabled = haptic, modifier = Modifier.size((38 * scale).dp)) { isDown ->
                onKey("m", "KeyM", 77, isDown)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Weapon Slots 1-5
            val weapons = listOf("1" to 49, "2" to 50, "3" to 51, "4" to 52, "5" to 53)
            weapons.forEach { (num, code) ->
                GamerTouchKey(num, null, Color(0xFF263238), Color(0xFF69F0AE), hapticEnabled = haptic, modifier = Modifier.size((34 * scale).dp)) { isDown ->
                    onKey(num, "Digit$num", code, isDown)
                }
            }
        }

        // CENTER/BOTTOM CONTENT BASED ON MODE
        when (settings.mode) {
            GamepadMode.GTA_V_COMBAT -> {
                CombatLayout(scale, haptic, onKey, onMouseClick, onMouseMove)
            }
            GamepadMode.GTA_V_DRIVING -> {
                DrivingLayout(scale, haptic, onKey, onMouseClick, onMouseMove)
            }
            GamepadMode.COMPACT -> {
                CompactLayout(scale, haptic, onKey, onMouseClick, onMouseMove)
            }
        }
    }
}

@Composable
private fun BoxScope.CombatLayout(
    scale: Float,
    haptic: Boolean,
    onKey: (String, String, Int, Boolean) -> Unit,
    onMouseClick: (Int, Boolean) -> Unit,
    onMouseMove: (Float, Float) -> Unit
) {
    // Left Side: WASD + Modifiers
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 6.dp, start = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // WASD Cluster
        Box(modifier = Modifier.size((140 * scale).dp)) {
            // W (Forward)
            GamerTouchKey("W", "Tiến", Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((44 * scale).dp).align(Alignment.TopCenter)) { isDown ->
                onKey("w", "KeyW", 87, isDown)
            }
            // A (Left)
            GamerTouchKey("A", "Trái", Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((44 * scale).dp).align(Alignment.CenterStart)) { isDown ->
                onKey("a", "KeyA", 65, isDown)
            }
            // S (Back)
            GamerTouchKey("S", "Lùi", Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((44 * scale).dp).align(Alignment.BottomCenter)) { isDown ->
                onKey("s", "KeyS", 83, isDown)
            }
            // D (Right)
            GamerTouchKey("D", "Phải", Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((44 * scale).dp).align(Alignment.CenterEnd)) { isDown ->
                onKey("d", "KeyD", 68, isDown)
            }
        }

        // Shift, Ctrl, Space row
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GamerTouchKey("CTRL", "Ngồi", Color(0xFF334155), Color(0xFF94A3B8), hapticEnabled = haptic, modifier = Modifier.width((50 * scale).dp).height((38 * scale).dp)) { isDown ->
                onKey("Control", "ControlLeft", 17, isDown)
            }
            GamerTouchKey("SHIFT", "Chạy", Color(0xFF0284C7), Color(0xFF38BDF8), hapticEnabled = haptic, modifier = Modifier.width((62 * scale).dp).height((38 * scale).dp)) { isDown ->
                onKey("Shift", "ShiftLeft", 16, isDown)
            }
            GamerTouchKey("SPACE", "Nhảy", Color(0xFFD97706), Color(0xFFFBBF24), hapticEnabled = haptic, modifier = Modifier.width((78 * scale).dp).height((38 * scale).dp)) { isDown ->
                onKey(" ", "Space", 32, isDown)
            }
        }
    }

    // Right Side: Action Keys + Aim/Shoot + Touchpad
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 6.dp, end = 6.dp)
            .size((220 * scale).dp, (170 * scale).dp)
    ) {
        // Quick Action Row (Q, E, F, R, G)
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            GamerTouchKey("Q", "Nấp", Color(0xFF475569), Color(0xFFCBD5E1), hapticEnabled = haptic, modifier = Modifier.size((36 * scale).dp)) { isDown ->
                onKey("q", "KeyQ", 81, isDown)
            }
            GamerTouchKey("E", "Tương tác", Color(0xFF475569), Color(0xFFCBD5E1), hapticEnabled = haptic, modifier = Modifier.size((36 * scale).dp)) { isDown ->
                onKey("e", "KeyE", 69, isDown)
            }
            GamerTouchKey("F", "Xe", Color(0xFFD97706), Color(0xFFF59E0B), hapticEnabled = haptic, modifier = Modifier.size((38 * scale).dp)) { isDown ->
                onKey("f", "KeyF", 70, isDown)
            }
            GamerTouchKey("R", "Đạn", Color(0xFFDC2626), Color(0xFFEF4444), hapticEnabled = haptic, modifier = Modifier.size((36 * scale).dp)) { isDown ->
                onKey("r", "KeyR", 82, isDown)
            }
            GamerTouchKey("G", "Bom", Color(0xFFEA580C), Color(0xFFF97316), hapticEnabled = haptic, modifier = Modifier.size((36 * scale).dp)) { isDown ->
                onKey("g", "KeyG", 71, isDown)
            }
        }

        // Camera Touchpad
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height((75 * scale).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
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
                text = "⚡ Vuốt xoay camera / ngắm",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Shoot (Left Click) & Aim (Right Click)
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GamerTouchKey(
                label = "🎯 NGẮM",
                subLabel = "R-Click",
                baseColor = Color(0xFF1E3A8A),
                activeColor = Color(0xFF3B82F6),
                hapticEnabled = haptic,
                modifier = Modifier.weight(1f).height((42 * scale).dp)
            ) { isDown ->
                onMouseClick(2, isDown)
            }

            GamerTouchKey(
                label = "🔥 BẮN",
                subLabel = "L-Click",
                baseColor = Color(0xFF991B1B),
                activeColor = Color(0xFFEF4444),
                hapticEnabled = haptic,
                modifier = Modifier.weight(1f).height((42 * scale).dp)
            ) { isDown ->
                onMouseClick(0, isDown)
            }
        }
    }
}

@Composable
private fun BoxScope.DrivingLayout(
    scale: Float,
    haptic: Boolean,
    onKey: (String, String, Int, Boolean) -> Unit,
    onMouseClick: (Int, Boolean) -> Unit,
    onMouseMove: (Float, Float) -> Unit
) {
    // Left: Steering Pedals
    Row(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 8.dp, start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Steer Left / Right
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GamerTouchKey("◄ A", "Rẽ trái", Color(0xFF1E293B), Color(0xFF38BDF8), hapticEnabled = haptic, modifier = Modifier.size((56 * scale).dp)) { isDown ->
                    onKey("a", "KeyA", 65, isDown)
                }
                GamerTouchKey("D ►", "Rẽ phải", Color(0xFF1E293B), Color(0xFF38BDF8), hapticEnabled = haptic, modifier = Modifier.size((56 * scale).dp)) { isDown ->
                    onKey("d", "KeyD", 68, isDown)
                }
            }
            GamerTouchKey("CÒI (E)", "Bấm còi xe", Color(0xFF334155), Color(0xFFFACC15), hapticEnabled = haptic, modifier = Modifier.fillMaxWidth().height((36 * scale).dp)) { isDown ->
                onKey("e", "KeyE", 69, isDown)
            }
        }

        // Gas (W) & Brake (S) Pedals
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            GamerTouchKey("⚡ GA (W)", "Tăng tốc", Color(0xFF065F46), Color(0xFF10B981), hapticEnabled = haptic, modifier = Modifier.width((72 * scale).dp).height((54 * scale).dp)) { isDown ->
                onKey("w", "KeyW", 87, isDown)
            }
            GamerTouchKey("🛑 PHANH (S)", "Lùi / Dừng", Color(0xFF991B1B), Color(0xFFF43F5E), hapticEnabled = haptic, modifier = Modifier.width((72 * scale).dp).height((46 * scale).dp)) { isDown ->
                onKey("s", "KeyS", 83, isDown)
            }
        }
    }

    // Right: Handbrake, Exit Vehicle, Nitro, Radio
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.End
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GamerTouchKey("RADIO (Q)", null, Color(0xFF475569), Color(0xFFA855F7), hapticEnabled = haptic, modifier = Modifier.size((44 * scale).dp)) { isDown ->
                onKey("q", "KeyQ", 81, isDown)
            }
            GamerTouchKey("NITRO (Shift)", null, Color(0xFF0369A1), Color(0xFF38BDF8), hapticEnabled = haptic, modifier = Modifier.width((90 * scale).dp).height((44 * scale).dp)) { isDown ->
                onKey("Shift", "ShiftLeft", 16, isDown)
            }
            GamerTouchKey("🚪 XUỐNG XE (F)", null, Color(0xFFD97706), Color(0xFFFBBF24), hapticEnabled = haptic, modifier = Modifier.width((110 * scale).dp).height((44 * scale).dp)) { isDown ->
                onKey("f", "KeyF", 70, isDown)
            }
        }

        // Big Handbrake Space bar
        GamerTouchKey(
            label = "🏁 THẮNG TAY (SPACE)",
            subLabel = "Drift xe cực mượt",
            baseColor = Color(0xFFB45309),
            activeColor = Color(0xFFF59E0B),
            hapticEnabled = haptic,
            modifier = Modifier.width((250 * scale).dp).height((50 * scale).dp)
        ) { isDown ->
            onKey(" ", "Space", 32, isDown)
        }
    }
}

@Composable
private fun BoxScope.CompactLayout(
    scale: Float,
    haptic: Boolean,
    onKey: (String, String, Int, Boolean) -> Unit,
    onMouseClick: (Int, Boolean) -> Unit,
    onMouseMove: (Float, Float) -> Unit
) {
    // Minimalist HUD
    Row(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 6.dp, start = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GamerTouchKey("W", null, Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((40 * scale).dp)) { isDown -> onKey("w", "KeyW", 87, isDown) }
        GamerTouchKey("A", null, Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((40 * scale).dp)) { isDown -> onKey("a", "KeyA", 65, isDown) }
        GamerTouchKey("S", null, Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((40 * scale).dp)) { isDown -> onKey("s", "KeyS", 83, isDown) }
        GamerTouchKey("D", null, Color(0xFF1E293B), Color(0xFF00E5FF), hapticEnabled = haptic, modifier = Modifier.size((40 * scale).dp)) { isDown -> onKey("d", "KeyD", 68, isDown) }
        GamerTouchKey("F", null, Color(0xFFD97706), Color(0xFFFBBF24), hapticEnabled = haptic, modifier = Modifier.size((40 * scale).dp)) { isDown -> onKey("f", "KeyF", 70, isDown) }
        GamerTouchKey("SPACE", null, Color(0xFFB45309), Color(0xFFF59E0B), hapticEnabled = haptic, modifier = Modifier.width((60 * scale).dp).height((40 * scale).dp)) { isDown -> onKey(" ", "Space", 32, isDown) }
    }

    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 6.dp, end = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GamerTouchKey("NGẮM", null, Color(0xFF1E3A8A), Color(0xFF3B82F6), hapticEnabled = haptic, modifier = Modifier.width((65 * scale).dp).height((40 * scale).dp)) { isDown -> onMouseClick(2, isDown) }
        GamerTouchKey("BẮN", null, Color(0xFF991B1B), Color(0xFFEF4444), hapticEnabled = haptic, modifier = Modifier.width((65 * scale).dp).height((40 * scale).dp)) { isDown -> onMouseClick(0, isDown) }
    }
}
