package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VirtualButtonData
import java.util.UUID

data class MiniKey(
    val label: String,
    val key: String,
    val code: String,
    val keyCode: Int,
    val widthFactor: Float = 1f,
    val isSpecial: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddButtonDialog(
    onDismiss: () -> Unit,
    onAddButton: (VirtualButtonData) -> Unit
) {
    var selectedSize by remember { mutableFloatStateOf(48f) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mini Full Keyboard, 1: GTA V Quick Presets

    val functionRow = remember {
        listOf(
            MiniKey("ESC", "Escape", "Escape", 27, 1.2f, true),
            MiniKey("F1", "F1", "F1", 112), MiniKey("F2", "F2", "F2", 113),
            MiniKey("F3", "F3", "F3", 114), MiniKey("F4", "F4", "F4", 115),
            MiniKey("F5", "F5", "F5", 116), MiniKey("F6", "F6", "F6", 117),
            MiniKey("F7", "F7", "F7", 118), MiniKey("F8", "F8", "F8", 119),
            MiniKey("F9", "F9", "F9", 120), MiniKey("F10", "F10", "F10", 121),
            MiniKey("F11", "F11", "F11", 122), MiniKey("F12", "F12", "F12", 123)
        )
    }

    val numberRow = remember {
        listOf(
            MiniKey("`", "`", "Backquote", 192),
            MiniKey("1", "1", "Digit1", 49), MiniKey("2", "2", "Digit2", 50),
            MiniKey("3", "3", "Digit3", 51), MiniKey("4", "4", "Digit4", 52),
            MiniKey("5", "5", "Digit5", 53), MiniKey("6", "6", "Digit6", 54),
            MiniKey("7", "7", "Digit7", 55), MiniKey("8", "8", "Digit8", 56),
            MiniKey("9", "9", "Digit9", 57), MiniKey("0", "0", "Digit0", 48),
            MiniKey("-", "-", "Minus", 189), MiniKey("=", "=", "Equal", 187),
            MiniKey("BACKSPACE", "Backspace", "Backspace", 8, 1.8f, true)
        )
    }

    val qwertyRow = remember {
        listOf(
            MiniKey("TAB", "Tab", "Tab", 9, 1.4f, true),
            MiniKey("Q", "q", "KeyQ", 81), MiniKey("W", "w", "KeyW", 87),
            MiniKey("E", "e", "KeyE", 69), MiniKey("R", "r", "KeyR", 82),
            MiniKey("T", "t", "KeyT", 84), MiniKey("Y", "y", "KeyY", 89),
            MiniKey("U", "u", "KeyU", 85), MiniKey("I", "i", "KeyI", 73),
            MiniKey("O", "o", "KeyO", 79), MiniKey("P", "p", "KeyP", 80),
            MiniKey("[", "[", "BracketLeft", 219), MiniKey("]", "]", "BracketRight", 221),
            MiniKey("\\", "\\", "Backslash", 220)
        )
    }

    val asdfRow = remember {
        listOf(
            MiniKey("CAPS", "CapsLock", "CapsLock", 20, 1.6f, true),
            MiniKey("A", "a", "KeyA", 65), MiniKey("S", "s", "KeyS", 83),
            MiniKey("D", "d", "KeyD", 68), MiniKey("F", "f", "KeyF", 70),
            MiniKey("G", "g", "KeyG", 71), MiniKey("H", "h", "KeyH", 72),
            MiniKey("J", "j", "KeyJ", 74), MiniKey("K", "k", "KeyK", 75),
            MiniKey("L", "l", "KeyL", 76), MiniKey(";", ";", "Semicolon", 186),
            MiniKey("'", "'", "Quote", 222),
            MiniKey("ENTER", "Enter", "Enter", 13, 1.8f, true)
        )
    }

    val zxcvRow = remember {
        listOf(
            MiniKey("SHIFT", "Shift", "ShiftLeft", 16, 2.0f, true),
            MiniKey("Z", "z", "KeyZ", 90), MiniKey("X", "x", "KeyX", 88),
            MiniKey("C", "c", "KeyC", 67), MiniKey("V", "v", "KeyV", 86),
            MiniKey("B", "b", "KeyB", 66), MiniKey("N", "n", "KeyN", 78),
            MiniKey("M", "m", "KeyM", 77), MiniKey(",", ",", "Comma", 188),
            MiniKey(".", ".", "Period", 190), MiniKey("/", "/", "Slash", 191),
            MiniKey("▲", "ArrowUp", "ArrowUp", 38, 1.1f, true)
        )
    }

    val bottomRow = remember {
        listOf(
            MiniKey("CTRL", "Control", "ControlLeft", 17, 1.5f, true),
            MiniKey("ALT", "Alt", "AltLeft", 18, 1.4f, true),
            MiniKey("SPACE", " ", "Space", 32, 4.5f, true),
            MiniKey("ALT", "Alt", "AltRight", 18, 1.4f, true),
            MiniKey("CTRL", "Control", "ControlRight", 17, 1.5f, true),
            MiniKey("◄", "ArrowLeft", "ArrowLeft", 37, 1.1f, true),
            MiniKey("▼", "ArrowDown", "ArrowDown", 40, 1.1f, true),
            MiniKey("►", "ArrowRight", "ArrowRight", 39, 1.1f, true)
        )
    }

    val gtaPresets = remember {
        listOf(
            Triple("H", "Bật/Tắt Đèn Xe", 72),
            Triple("V", "Đổi Góc Nhìn", 86),
            Triple("X", "Cúi Đầu Trong Xe", 88),
            Triple("C", "Nhìn Về Phía Sau", 67),
            Triple("Z", "Phóng To Bản Đồ", 90),
            Triple("ENTER", "Xác Nhận / Lên Xuống", 13),
            Triple("B", "Chỉ Trỏ / Chỉ Tay", 66),
            Triple("N", "Nói Bộ Đàm (Voice)", 78),
            Triple("P", "Tạm Dừng / Bản Đồ", 80),
            Triple("K", "Khóa Cửa Xe / Đai", 75),
            Triple("L", "Còi Hú / Xi Nhan", 76),
            Triple("T", "Mở Chat FiveM", 84)
        )
    }

    fun addKey(keyData: MiniKey) {
        val colorHex = if (keyData.isSpecial) "#0284C7" else "#1E293B"
        val activeColorHex = if (keyData.isSpecial) "#38BDF8" else "#00E5FF"
        val width = if (keyData.widthFactor > 1.2f) selectedSize * (keyData.widthFactor * 0.75f) else selectedSize
        val newBtn = VirtualButtonData(
            id = "custom_${UUID.randomUUID().toString().take(6)}",
            label = keyData.label,
            subLabel = null,
            key = keyData.key,
            code = keyData.code,
            keyCode = keyData.keyCode,
            xPercent = 0.50f,
            yPercent = 0.45f,
            widthDp = width,
            heightDp = selectedSize * 0.88f,
            isRemovable = true,
            colorHex = colorHex,
            activeColorHex = activeColorHex
        )
        onAddButton(newBtn)
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Chọn phím PC để thêm vào màn hình", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Size Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cỡ nút:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedSize == 42f,
                            onClick = { selectedSize = 42f },
                            label = { Text("Nhỏ (42dp)", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = selectedSize == 50f,
                            onClick = { selectedSize = 50f },
                            label = { Text("Vừa (50dp)", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = selectedSize == 62f,
                            onClick = { selectedSize = 62f },
                            label = { Text("Lớn (62dp)", fontSize = 10.sp) }
                        )
                    }
                }

                // Tab Switcher: Full PC Keyboard vs GTA V Presets
                TabRow(selectedTabIndex = selectedTab, modifier = Modifier.height(38.dp)) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Bàn phím PC Full-size", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Phím GTA V Gợi ý", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                if (selectedTab == 0) {
                    // MINI FULL PC KEYBOARD VIEW (Scrollable horizontally)
                    val hScroll = rememberScrollState()
                    val vScroll = rememberScrollState()

                    Text("👉 Chạm trực tiếp vào phím bất kỳ trên bàn phím bên dưới để thêm:", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .horizontalScroll(hScroll)
                                .verticalScroll(vScroll),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Function Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                functionRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                            // Numbers Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                numberRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                            // QWERTY Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                qwertyRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                            // ASDF Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                asdfRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                            // ZXCV Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                zxcvRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                            // Bottom Space Row
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                bottomRow.forEach { key ->
                                    MiniKeyboardKeyView(key) { addKey(key) }
                                }
                            }
                        }
                    }
                } else {
                    // GTA V QUICK PRESETS
                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(scroll),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gtaPresets.forEach { (char, desc, code) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        addKey(
                                            MiniKey(char, char.lowercase(), if (char.length == 1) "Key$char" else char, code, isSpecial = true)
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF0284C7),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(char, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }
                                        Text(desc, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                    }
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun MiniKeyboardKeyView(
    key: MiniKey,
    onClick: () -> Unit
) {
    val baseWidth = 34.dp
    val width = baseWidth * key.widthFactor

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = if (key.isSpecial) Color(0xFF1E3A8A) else Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (key.isSpecial) Color(0xFF3B82F6) else Color(0xFF475569)),
        modifier = Modifier
            .width(width)
            .height(30.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = key.label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (key.label.length > 3) 8.sp else 10.sp,
                maxLines = 1
            )
        }
    }
}
