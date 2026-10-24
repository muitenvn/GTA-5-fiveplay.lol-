package com.example.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ControlType
import com.example.model.LayoutProfile
import com.example.model.VirtualButtonData
import org.json.JSONArray
import org.json.JSONObject

object LayoutRepository {

    private const val PREFS_NAME = "fiveplay_layout_prefs"
    private const val KEY_CUSTOM_KEYBOARD = "custom_keyboard_buttons"
    private const val KEY_CUSTOM_GAMEPAD = "custom_gamepad_buttons"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadButtons(context: Context, controlType: ControlType, profile: LayoutProfile): MutableList<VirtualButtonData> {
        if (profile == LayoutProfile.DEFAULT) {
            return if (controlType == ControlType.KEYBOARD) {
                getDefaultKeyboardButtons().toMutableList()
            } else {
                getDefaultGamepadButtons().toMutableList()
            }
        }

        // Custom Profile: try to load from SharedPreferences
        val prefs = getPrefs(context)
        val key = if (controlType == ControlType.KEYBOARD) KEY_CUSTOM_KEYBOARD else KEY_CUSTOM_GAMEPAD
        val jsonStr = prefs.getString(key, null)

        return if (!jsonStr.isNullOrEmpty()) {
            try {
                deserializeButtons(jsonStr)
            } catch (e: Exception) {
                if (controlType == ControlType.KEYBOARD) getDefaultKeyboardButtons().toMutableList()
                else getDefaultGamepadButtons().toMutableList()
            }
        } else {
            // Initial custom starts with a clone of default buttons
            val defaults = if (controlType == ControlType.KEYBOARD) getDefaultKeyboardButtons() else getDefaultGamepadButtons()
            defaults.toMutableList()
        }
    }

    fun saveButtons(context: Context, controlType: ControlType, buttons: List<VirtualButtonData>) {
        val prefs = getPrefs(context)
        val key = if (controlType == ControlType.KEYBOARD) KEY_CUSTOM_KEYBOARD else KEY_CUSTOM_GAMEPAD
        val jsonStr = serializeButtons(buttons)
        prefs.edit().putString(key, jsonStr).apply()
    }

    fun resetToDefault(context: Context, controlType: ControlType): MutableList<VirtualButtonData> {
        val prefs = getPrefs(context)
        val key = if (controlType == ControlType.KEYBOARD) KEY_CUSTOM_KEYBOARD else KEY_CUSTOM_GAMEPAD
        prefs.edit().remove(key).apply()
        return if (controlType == ControlType.KEYBOARD) {
            getDefaultKeyboardButtons().toMutableList()
        } else {
            getDefaultGamepadButtons().toMutableList()
        }
    }

    private fun serializeButtons(buttons: List<VirtualButtonData>): String {
        val array = JSONArray()
        for (btn in buttons) {
            val obj = JSONObject()
            obj.put("id", btn.id)
            obj.put("label", btn.label)
            obj.put("subLabel", btn.subLabel)
            obj.put("key", btn.key)
            obj.put("code", btn.code)
            obj.put("keyCode", btn.keyCode)
            obj.put("xPercent", btn.xPercent.toDouble())
            obj.put("yPercent", btn.yPercent.toDouble())
            obj.put("widthDp", btn.widthDp.toDouble())
            obj.put("heightDp", btn.heightDp.toDouble())
            obj.put("isRemovable", btn.isRemovable)
            obj.put("colorHex", btn.colorHex)
            obj.put("activeColorHex", btn.activeColorHex)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeButtons(jsonStr: String): MutableList<VirtualButtonData> {
        val list = mutableListOf<VirtualButtonData>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                VirtualButtonData(
                    id = obj.getString("id"),
                    label = obj.getString("label"),
                    subLabel = if (obj.has("subLabel") && !obj.isNull("subLabel")) obj.getString("subLabel") else null,
                    key = obj.getString("key"),
                    code = obj.getString("code"),
                    keyCode = obj.getInt("keyCode"),
                    xPercent = obj.getDouble("xPercent").toFloat(),
                    yPercent = obj.getDouble("yPercent").toFloat(),
                    widthDp = obj.optDouble("widthDp", 48.0).toFloat(),
                    heightDp = obj.optDouble("heightDp", 44.0).toFloat(),
                    isRemovable = obj.optBoolean("isRemovable", true),
                    colorHex = obj.optString("colorHex", "#1E293B"),
                    activeColorHex = obj.optString("activeColorHex", "#00E5FF")
                )
            )
        }
        return list
    }

    fun getDefaultKeyboardButtons(): List<VirtualButtonData> {
        return listOf(
            // Movement: WASD
            VirtualButtonData("k_w", "W", "Tiến", "w", "KeyW", 87, 0.08f, 0.50f, 52f, 48f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("k_a", "A", "Trái", "a", "KeyA", 65, 0.02f, 0.63f, 52f, 48f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("k_s", "S", "Lùi", "s", "KeyS", 83, 0.08f, 0.63f, 52f, 48f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("k_d", "D", "Phải", "d", "KeyD", 68, 0.14f, 0.63f, 52f, 48f, false, "#1E293B", "#00E5FF"),

            // Modifiers
            VirtualButtonData("k_shift", "SHIFT", "Chạy", "Shift", "ShiftLeft", 16, 0.02f, 0.77f, 66f, 42f, false, "#0284C7", "#38BDF8"),
            VirtualButtonData("k_ctrl", "CTRL", "Ngồi", "Control", "ControlLeft", 17, 0.09f, 0.77f, 54f, 42f, true, "#334155", "#94A3B8"),
            VirtualButtonData("k_space", "SPACE", "Nhảy / Phanh", " ", "Space", 32, 0.15f, 0.77f, 86f, 42f, false, "#D97706", "#FBBF24"),

            // Action keys (Right)
            VirtualButtonData("k_f", "F", "Lên xe", "f", "KeyF", 70, 0.86f, 0.44f, 50f, 46f, false, "#D97706", "#F59E0B"),
            VirtualButtonData("k_r", "R", "Nạp đạn", "r", "KeyR", 82, 0.92f, 0.44f, 48f, 46f, true, "#DC2626", "#EF4444"),
            VirtualButtonData("k_g", "G", "Lựu đạn", "g", "KeyG", 71, 0.80f, 0.44f, 48f, 46f, true, "#EA580C", "#F97316"),
            VirtualButtonData("k_e", "E", "Còi/HĐ", "e", "KeyE", 69, 0.74f, 0.44f, 48f, 46f, true, "#475569", "#CBD5E1"),
            VirtualButtonData("k_q", "Q", "Nấp/Radio", "q", "KeyQ", 81, 0.68f, 0.44f, 48f, 46f, true, "#475569", "#CBD5E1"),

            // Special user requested: ĐÈN XE (H)
            VirtualButtonData("k_h", "H", "Đèn xe", "h", "KeyH", 72, 0.68f, 0.54f, 50f, 44f, true, "#4F46E5", "#818CF8"),
            VirtualButtonData("k_v", "V", "Góc nhìn", "v", "KeyV", 86, 0.74f, 0.54f, 50f, 44f, true, "#059669", "#34D399"),

            // Top Menu & Number row
            VirtualButtonData("k_esc", "ESC", "Thoát", "Escape", "Escape", 27, 0.30f, 0.05f, 44f, 38f, false, "#37474F", "#FF5252"),
            VirtualButtonData("k_tab", "TAB", "Vũ khí", "Tab", "Tab", 9, 0.36f, 0.05f, 44f, 38f, false, "#37474F", "#40C4FF"),
            VirtualButtonData("k_m", "M", "Menu", "m", "KeyM", 77, 0.42f, 0.05f, 44f, 38f, false, "#37474F", "#FFD740"),
            VirtualButtonData("k_1", "1", null, "1", "Digit1", 49, 0.50f, 0.05f, 38f, 38f, true, "#263238", "#69F0AE"),
            VirtualButtonData("k_2", "2", null, "2", "Digit2", 50, 0.55f, 0.05f, 38f, 38f, true, "#263238", "#69F0AE"),
            VirtualButtonData("k_3", "3", null, "3", "Digit3", 51, 0.60f, 0.05f, 38f, 38f, true, "#263238", "#69F0AE"),
            VirtualButtonData("k_4", "4", null, "4", "Digit4", 52, 0.65f, 0.05f, 38f, 38f, true, "#263238", "#69F0AE")
        )
    }

    fun getDefaultGamepadButtons(): List<VirtualButtonData> {
        return listOf(
            // D-Pad
            VirtualButtonData("gp_up", "▲", "Lên", "ArrowUp", "ArrowUp", 38, 0.08f, 0.52f, 48f, 44f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("gp_left", "◄", "Trái", "ArrowLeft", "ArrowLeft", 37, 0.02f, 0.64f, 48f, 44f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("gp_down", "▼", "Xuống", "ArrowDown", "ArrowDown", 40, 0.08f, 0.64f, 48f, 44f, false, "#1E293B", "#00E5FF"),
            VirtualButtonData("gp_right", "►", "Phải", "ArrowRight", "ArrowRight", 39, 0.14f, 0.64f, 48f, 44f, false, "#1E293B", "#00E5FF"),

            // Shoulders & Triggers
            VirtualButtonData("gp_l1", "L1", "Ngắm", "Control", "ControlLeft", 17, 0.02f, 0.10f, 62f, 40f, false, "#334155", "#38BDF8"),
            VirtualButtonData("gp_l2", "L2", "Phanh", "s", "KeyS", 83, 0.10f, 0.10f, 62f, 40f, false, "#991B1B", "#F43F5E"),
            VirtualButtonData("gp_r1", "R1", "Bắn", " ", "Space", 32, 0.82f, 0.10f, 62f, 40f, false, "#065F46", "#10B981"),
            VirtualButtonData("gp_r2", "R2", "Ga", "w", "KeyW", 87, 0.90f, 0.10f, 62f, 40f, false, "#0284C7", "#00E5FF"),

            // Action ABXY Buttons (Right)
            VirtualButtonData("gp_y", "Y", "Tam giác", "f", "KeyF", 70, 0.88f, 0.50f, 48f, 48f, false, "#166534", "#22C55E"),
            VirtualButtonData("gp_x", "X", "Vuông", "r", "KeyR", 82, 0.82f, 0.62f, 48f, 48f, false, "#1E40AF", "#3B82F6"),
            VirtualButtonData("gp_b", "B", "Tròn", "Control", "ControlLeft", 17, 0.94f, 0.62f, 48f, 48f, false, "#991B1B", "#EF4444"),
            VirtualButtonData("gp_a", "A", "Chéo", " ", "Space", 32, 0.88f, 0.74f, 48f, 48f, false, "#854D0E", "#EAB308"),

            // Center / Utility
            VirtualButtonData("gp_select", "SELECT", "Bản đồ", "m", "KeyM", 77, 0.38f, 0.08f, 65f, 36f, false, "#37474F", "#94A3B8"),
            VirtualButtonData("gp_start", "START", "Tạm dừng", "Escape", "Escape", 27, 0.52f, 0.08f, 65f, 36f, false, "#37474F", "#94A3B8"),

            // Extra GTA V Shortcut (Headlights)
            VirtualButtonData("gp_h", "ĐÈN (H)", null, "h", "KeyH", 72, 0.72f, 0.50f, 54f, 42f, true, "#4F46E5", "#818CF8")
        )
    }
}
