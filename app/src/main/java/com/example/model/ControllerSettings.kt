package com.example.model

data class ControllerSettings(
    val opacity: Float = 0.85f,
    val hapticFeedback: Boolean = true,
    val sensitivity: Float = 1.0f,
    val layoutStyle: String = "GTA_V_PRO", // XBOX, PLAYSTATION, GTA_V_PRO
    val showShortcutsBar: Boolean = true,
    val turboMode: Boolean = false
)
