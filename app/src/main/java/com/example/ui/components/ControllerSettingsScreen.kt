package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ControllerSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerSettingsScreen(
    settings: ControllerSettings,
    onSettingsChanged: (ControllerSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Controller Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Customize your virtual gamepad opacity, haptics, and layout for playing GTA V on fiveplay.lol.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Opacity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Overlay Transparency (Opacity)", fontWeight = FontWeight.SemiBold)
                    Text(text = "${(settings.opacity * 100).toInt()}%")
                }
                Slider(
                    value = settings.opacity,
                    onValueChange = { onSettingsChanged(settings.copy(opacity = it)) },
                    valueRange = 0.3f..1.0f,
                    modifier = Modifier.testTag("opacity_slider")
                )
            }
        }

        // Haptic Feedback Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Haptic Feedback (Vibration)", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Vibrate phone slightly when pressing controller buttons.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.hapticFeedback,
                    onCheckedChange = { onSettingsChanged(settings.copy(hapticFeedback = it)) },
                    modifier = Modifier.testTag("haptic_switch")
                )
            }
        }

        // Joystick Sensitivity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Joystick Sensitivity", fontWeight = FontWeight.SemiBold)
                    Text(text = String.format("%.1fx", settings.sensitivity))
                }
                Slider(
                    value = settings.sensitivity,
                    onValueChange = { onSettingsChanged(settings.copy(sensitivity = it)) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.testTag("sensitivity_slider")
                )
            }
        }

        // Shortcuts Bar Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "GTA V PC Shortcuts Bar", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Show quick buttons for F (Vehicle), Shift (Sprint), Space, Tab, M, Esc.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.showShortcutsBar,
                    onCheckedChange = { onSettingsChanged(settings.copy(showShortcutsBar = it)) },
                    modifier = Modifier.testTag("shortcuts_switch")
                )
            }
        }

        // Layout Style Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Controller Layout Style", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = settings.layoutStyle == "GTA_V_PRO",
                        onClick = { onSettingsChanged(settings.copy(layoutStyle = "GTA_V_PRO")) },
                        label = { Text("GTA V Pro") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.layoutStyle == "XBOX",
                        onClick = { onSettingsChanged(settings.copy(layoutStyle = "XBOX")) },
                        label = { Text("Xbox Pad") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.layoutStyle == "PLAYSTATION",
                        onClick = { onSettingsChanged(settings.copy(layoutStyle = "PLAYSTATION")) },
                        label = { Text("PS DualSense") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
