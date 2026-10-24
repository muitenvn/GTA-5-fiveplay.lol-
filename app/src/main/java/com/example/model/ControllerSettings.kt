package com.example.model

enum class GamepadMode {
    GTA_V_COMBAT,  // Movement WASD + Mouse Look + Combat/Weapons
    GTA_V_DRIVING, // Pedals (Gas W, Brake S, Steer A/D, Handbrake Space, Exit F, Horn E, Radio Q)
    COMPACT        // Minimalist transparent HUD
}

data class ControllerSettings(
    val opacity: Float = 0.80f,
    val hapticFeedback: Boolean = true,
    val buttonScale: Float = 1.0f,
    val mode: GamepadMode = GamepadMode.GTA_V_COMBAT,
    val mouseSensitivity: Float = 1.0f,
    val autoFitFullscreen: Boolean = true,
    val sensitivity: Float = 1.0f,
    val layoutStyle: String = "GTA_V_PRO",
    val showShortcutsBar: Boolean = true
)
