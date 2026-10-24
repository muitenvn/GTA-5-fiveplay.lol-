package com.example.model

enum class ControlType {
    KEYBOARD, // Bàn phím PC & Chuột
    GAMEPAD   // Tay cầm Console
}

enum class LayoutProfile {
    DEFAULT, // Bố cục Mặc định
    CUSTOM   // Bố cục Tùy chỉnh
}

data class VirtualButtonData(
    val id: String,
    val label: String,
    val subLabel: String? = null,
    val key: String,
    val code: String,
    val keyCode: Int,
    var xPercent: Float, // 0.0f .. 1.0f relative to screen width
    var yPercent: Float, // 0.0f .. 1.0f relative to screen height
    var widthDp: Float = 48f,
    var heightDp: Float = 44f,
    val isRemovable: Boolean = true,
    val colorHex: String = "#1E293B",
    val activeColorHex: String = "#00E5FF"
)
