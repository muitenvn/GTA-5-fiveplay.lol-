package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VirtualButtonData

@Composable
fun EditButtonDialog(
    button: VirtualButtonData,
    onDismiss: () -> Unit,
    onUpdateSize: (width: Float, height: Float) -> Unit,
    onDelete: () -> Unit
) {
    var width by remember { mutableFloatStateOf(button.widthDp) }
    var height by remember { mutableFloatStateOf(button.heightDp) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Chỉnh kích thước phím: ${button.label}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (button.subLabel != null) {
                    Text("Chức năng: ${button.subLabel}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Width slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Chiều rộng", fontSize = 12.sp)
                        Text("${width.toInt()} dp", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = width,
                        onValueChange = {
                            width = it
                            onUpdateSize(width, height)
                        },
                        valueRange = 36f..140f
                    )
                }

                // Height slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Chiều cao", fontSize = 12.sp)
                        Text("${height.toInt()} dp", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = height,
                        onValueChange = {
                            height = it
                            onUpdateSize(width, height)
                        },
                        valueRange = 32f..100f
                    )
                }

                if (button.isRemovable) {
                    Divider()
                    OutlinedButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xóa phím này khỏi màn hình")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Hoàn tất")
            }
        }
    )
}
