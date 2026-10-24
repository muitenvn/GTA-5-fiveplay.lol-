package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VirtualButtonData
import java.util.UUID

data class PresetKeyOption(
    val label: String,
    val subLabel: String,
    val key: String,
    val code: String,
    val keyCode: Int,
    val colorHex: String,
    val activeColorHex: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddButtonDialog(
    onDismiss: () -> Unit,
    onAddButton: (VirtualButtonData) -> Unit
) {
    var customLabel by remember { mutableStateOf("") }
    var selectedSize by remember { mutableFloatStateOf(50f) }

    val presetKeys = remember {
        listOf(
            PresetKeyOption("H", "Bật/Tắt Đèn Xe", "h", "KeyH", 72, "#4F46E5", "#818CF8"),
            PresetKeyOption("V", "Đổi Góc Nhìn (FPP/TPP)", "v", "KeyV", 86, "#059669", "#34D399"),
            PresetKeyOption("X", "Cúi Đầu Trong Xe", "x", "KeyX", 88, "#0284C7", "#38BDF8"),
            PresetKeyOption("C", "Nhìn Về Phía Sau", "c", "KeyC", 67, "#7C3AED", "#A78BFA"),
            PresetKeyOption("Z", "Phóng To Bản Đồ", "z", "KeyZ", 90, "#D97706", "#FBBF24"),
            PresetKeyOption("P", "Bản Đồ Lớn / Pause", "p", "KeyP", 80, "#DC2626", "#EF4444"),
            PresetKeyOption("ENTER", "Xác Nhận / Vào Việc", "Enter", "Enter", 13, "#059669", "#10B981"),
            PresetKeyOption("B", "Chỉ Trỏ / Chỉ Tay", "b", "KeyB", 66, "#475569", "#94A3B8"),
            PresetKeyOption("N", "Bộ Đàm / Voice Chat", "n", "KeyN", 78, "#2563EB", "#60A5FA"),
            PresetKeyOption("K", "Khóa Xe / Đai An Toàn", "k", "KeyK", 75, "#B45309", "#F59E0B"),
            PresetKeyOption("L", "Xi-nhan / Cảnh Báo", "l", "KeyL", 76, "#B91C1C", "#F87171"),
            PresetKeyOption("T", "Mở Khung Chat", "t", "KeyT", 84, "#475569", "#CBD5E1")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Thêm nút ảo vào màn hình", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Size Picker
                Text("Kích thước nút:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSize == 42f,
                        onClick = { selectedSize = 42f },
                        label = { Text("Nhỏ (42dp)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedSize == 52f,
                        onClick = { selectedSize = 52f },
                        label = { Text("Vừa (52dp)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedSize == 68f,
                        onClick = { selectedSize = 68f },
                        label = { Text("Lớn (68dp)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider()

                // List of preset GTA V keys
                Text("Chọn phím GTA V thông dụng:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presetKeys) { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newBtn = VirtualButtonData(
                                        id = "custom_${UUID.randomUUID().toString().take(6)}",
                                        label = preset.label,
                                        subLabel = preset.subLabel,
                                        key = preset.key,
                                        code = preset.code,
                                        keyCode = preset.keyCode,
                                        xPercent = 0.50f, // Center of screen
                                        yPercent = 0.50f,
                                        widthDp = selectedSize,
                                        heightDp = selectedSize * 0.88f,
                                        isRemovable = true,
                                        colorHex = preset.colorHex,
                                        activeColorHex = preset.activeColorHex
                                    )
                                    onAddButton(newBtn)
                                    onDismiss()
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
                                        color = Color(android.graphics.Color.parseColor(preset.colorHex)),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(preset.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Column {
                                        Text(preset.subLabel, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                        Text("Phím: ${preset.key.uppercase()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Custom key input field
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it.take(4).uppercase() },
                    label = { Text("Hoặc nhập phím tùy ý (ví dụ: Y, U, J, F1)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            if (customLabel.isNotBlank()) {
                Button(
                    onClick = {
                        val keyChar = customLabel.firstOrNull()?.toString() ?: "A"
                        val keyCode = keyChar.first().code
                        val newBtn = VirtualButtonData(
                            id = "custom_${UUID.randomUUID().toString().take(6)}",
                            label = customLabel,
                            subLabel = "Phím tự tạo",
                            key = keyChar.lowercase(),
                            code = "Key$keyChar",
                            keyCode = keyCode,
                            xPercent = 0.50f,
                            yPercent = 0.50f,
                            widthDp = selectedSize,
                            heightDp = selectedSize * 0.88f,
                            isRemovable = true,
                            colorHex = "#334155",
                            activeColorHex = "#38BDF8"
                        )
                        onAddButton(newBtn)
                        onDismiss()
                    }
                ) {
                    Text("Thêm phím: $customLabel")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
