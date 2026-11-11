package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LauncherPreferencesRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<LauncherPreferences> = _preferences.asStateFlow()

    private val _pinnedDockApps = MutableStateFlow(loadStringSet(KEY_PINNED_DOCK_APPS))
    val pinnedDockApps: StateFlow<Set<String>> = _pinnedDockApps.asStateFlow()

    private val _pinnedHomeApps = MutableStateFlow(loadStringSet(KEY_PINNED_HOME_APPS))
    val pinnedHomeApps: StateFlow<Set<String>> = _pinnedHomeApps.asStateFlow()

    private val _hiddenApps = MutableStateFlow(loadStringSet(KEY_HIDDEN_APPS))
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps.asStateFlow()

    private val _customLabels = MutableStateFlow(loadMap(KEY_CUSTOM_LABELS))
    val customLabels: StateFlow<Map<String, String>> = _customLabels.asStateFlow()

    private val _widgetConfigs = MutableStateFlow(loadWidgetConfigs())
    val widgetConfigs: StateFlow<List<WidgetConfig>> = _widgetConfigs.asStateFlow()

    fun updatePreferences(update: (LauncherPreferences) -> LauncherPreferences) {
        val current = _preferences.value
        val updated = update(current)
        _preferences.value = updated
        savePreferences(updated)
    }

    fun togglePinDock(packageName: String) {
        val current = _pinnedDockApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            if (current.size < _preferences.value.dockAppLimit) {
                current.add(packageName)
            }
        }
        _pinnedDockApps.value = current
        saveStringSet(KEY_PINNED_DOCK_APPS, current)
    }

    fun togglePinHome(packageName: String) {
        val current = _pinnedHomeApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _pinnedHomeApps.value = current
        saveStringSet(KEY_PINNED_HOME_APPS, current)
    }

    fun toggleHideApp(packageName: String) {
        val current = _hiddenApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _hiddenApps.value = current
        saveStringSet(KEY_HIDDEN_APPS, current)
    }

    fun setCustomLabel(packageName: String, customName: String) {
        val current = _customLabels.value.toMutableMap()
        if (customName.isBlank()) {
            current.remove(packageName)
        } else {
            current[packageName] = customName.trim()
        }
        _customLabels.value = current
        saveMap(KEY_CUSTOM_LABELS, current)
    }

    fun updateScratchpad(text: String) {
        updatePreferences { it.copy(scratchpadContent = text) }
    }

    fun updateWidgetConfig(type: WidgetType, isEnabled: Boolean, isCompact: Boolean) {
        val current = _widgetConfigs.value.map {
            if (it.type == type) it.copy(isEnabled = isEnabled, isCompact = isCompact) else it
        }
        _widgetConfigs.value = current
        saveWidgetConfigs(current)
    }

    fun reorderWidgets(newList: List<WidgetConfig>) {
        val ordered = newList.mapIndexed { index, config -> config.copy(order = index) }
        _widgetConfigs.value = ordered
        saveWidgetConfigs(ordered)
    }

    private fun loadPreferences(): LauncherPreferences {
        val iconThemeName = prefs.getString(KEY_ICON_THEME, BuiltInIconTheme.GRAPHENE_MONOCHROME.id)
        val iconTheme = BuiltInIconTheme.entries.find { it.id == iconThemeName }
            ?: BuiltInIconTheme.GRAPHENE_MONOCHROME

        val iconShapeName = prefs.getString(KEY_ICON_SHAPE, IconShape.SQUIRCLE.id)
        val iconShape = IconShape.entries.find { it.id == iconShapeName } ?: IconShape.SQUIRCLE

        val swipeUp = GestureAction.entries.find { it.id == prefs.getString(KEY_GESTURE_SWIPE_UP, GestureAction.OPEN_APP_DRAWER.id) }
            ?: GestureAction.OPEN_APP_DRAWER
        val swipeDown = GestureAction.entries.find { it.id == prefs.getString(KEY_GESTURE_SWIPE_DOWN, GestureAction.EXPAND_NOTIFICATIONS.id) }
            ?: GestureAction.EXPAND_NOTIFICATIONS
        val doubleTap = GestureAction.entries.find { it.id == prefs.getString(KEY_GESTURE_DOUBLE_TAP, GestureAction.LOCK_SCREEN.id) }
            ?: GestureAction.LOCK_SCREEN
        val pinchIn = GestureAction.entries.find { it.id == prefs.getString(KEY_GESTURE_PINCH, GestureAction.QUICK_SEARCH.id) }
            ?: GestureAction.QUICK_SEARCH
        val longPress = GestureAction.entries.find { it.id == prefs.getString(KEY_GESTURE_LONG_PRESS, GestureAction.OPEN_SETTINGS.id) }
            ?: GestureAction.OPEN_SETTINGS

        val weatherCondName = prefs.getString(KEY_WEATHER_COND, WeatherCondition.CLEAR_SKY.name)
        val weatherCond = try {
            WeatherCondition.valueOf(weatherCondName ?: WeatherCondition.CLEAR_SKY.name)
        } catch (_: Exception) {
            WeatherCondition.CLEAR_SKY
        }

        return LauncherPreferences(
            iconTheme = iconTheme,
            selectedCustomIconPackPackage = prefs.getString(KEY_CUSTOM_ICON_PACK, null),
            iconShape = iconShape,
            iconScalePercent = prefs.getInt(KEY_ICON_SCALE, 100),
            showAppLabels = prefs.getBoolean(KEY_SHOW_LABELS, true),
            gridColumns = prefs.getInt(KEY_GRID_COLUMNS, 4),
            dockAppLimit = prefs.getInt(KEY_DOCK_LIMIT, 5),
            isTrueAmoledBlack = prefs.getBoolean(KEY_AMOLED_BLACK, true),
            showAlphabetScrubber = prefs.getBoolean(KEY_SHOW_SCRUBBER, true),
            autoOpenKeyboardInDrawer = prefs.getBoolean(KEY_AUTO_KEYBOARD, false),
            gestures = GestureSettings(
                swipeUp = swipeUp,
                swipeDown = swipeDown,
                doubleTap = doubleTap,
                pinchIn = pinchIn,
                longPress = longPress
            ),
            scratchpadContent = prefs.getString(KEY_SCRATCHPAD, "Private scratchpad — stored locally on-device. Zero telemetry.") ?: "",
            weatherCity = prefs.getString(KEY_WEATHER_CITY, "Privacy Zone") ?: "Privacy Zone",
            weatherTempCelsius = prefs.getInt(KEY_WEATHER_TEMP, 21),
            weatherCondition = weatherCond,
            weatherUseFahrenheit = prefs.getBoolean(KEY_WEATHER_FAHRENHEIT, false)
        )
    }

    private fun savePreferences(p: LauncherPreferences) {
        prefs.edit()
            .putString(KEY_ICON_THEME, p.iconTheme.id)
            .putString(KEY_CUSTOM_ICON_PACK, p.selectedCustomIconPackPackage)
            .putString(KEY_ICON_SHAPE, p.iconShape.id)
            .putInt(KEY_ICON_SCALE, p.iconScalePercent)
            .putBoolean(KEY_SHOW_LABELS, p.showAppLabels)
            .putInt(KEY_GRID_COLUMNS, p.gridColumns)
            .putInt(KEY_DOCK_LIMIT, p.dockAppLimit)
            .putBoolean(KEY_AMOLED_BLACK, p.isTrueAmoledBlack)
            .putBoolean(KEY_SHOW_SCRUBBER, p.showAlphabetScrubber)
            .putBoolean(KEY_AUTO_KEYBOARD, p.autoOpenKeyboardInDrawer)
            .putString(KEY_GESTURE_SWIPE_UP, p.gestures.swipeUp.id)
            .putString(KEY_GESTURE_SWIPE_DOWN, p.gestures.swipeDown.id)
            .putString(KEY_GESTURE_DOUBLE_TAP, p.gestures.doubleTap.id)
            .putString(KEY_GESTURE_PINCH, p.gestures.pinchIn.id)
            .putString(KEY_GESTURE_LONG_PRESS, p.gestures.longPress.id)
            .putString(KEY_SCRATCHPAD, p.scratchpadContent)
            .putString(KEY_WEATHER_CITY, p.weatherCity)
            .putInt(KEY_WEATHER_TEMP, p.weatherTempCelsius)
            .putString(KEY_WEATHER_COND, p.weatherCondition.name)
            .putBoolean(KEY_WEATHER_FAHRENHEIT, p.weatherUseFahrenheit)
            .apply()
    }

    private fun loadWidgetConfigs(): List<WidgetConfig> {
        val serialized = prefs.getString(KEY_WIDGET_CONFIGS, null)
        if (serialized.isNullOrBlank()) {
            return listOf(
                WidgetConfig(WidgetType.CLOCK_DATE, isEnabled = true, order = 0, isCompact = false),
                WidgetConfig(WidgetType.PRIVACY_WEATHER, isEnabled = true, order = 1, isCompact = true),
                WidgetConfig(WidgetType.BATTERY_STATUS, isEnabled = true, order = 2, isCompact = true),
                WidgetConfig(WidgetType.PRIVACY_AUDIT, isEnabled = true, order = 3, isCompact = false),
                WidgetConfig(WidgetType.QUICK_ACTIONS, isEnabled = true, order = 4, isCompact = false),
                WidgetConfig(WidgetType.QUICK_SCRATCHPAD, isEnabled = false, order = 5, isCompact = false)
            )
        }
        val configs = mutableListOf<WidgetConfig>()
        val items = serialized.split(";")
        for ((idx, item) in items.withIndex()) {
            val parts = item.split(":")
            if (parts.size >= 3) {
                val type = WidgetType.entries.find { it.id == parts[0] } ?: continue
                val enabled = parts[1].toBoolean()
                val compact = parts[2].toBoolean()
                configs.add(WidgetConfig(type, enabled, idx, isCompact = compact))
            }
        }
        // Ensure all types exist
        for (type in WidgetType.entries) {
            if (configs.none { it.type == type }) {
                configs.add(WidgetConfig(type, isEnabled = false, order = configs.size))
            }
        }
        return configs.sortedBy { it.order }
    }

    private fun saveWidgetConfigs(configs: List<WidgetConfig>) {
        val serialized = configs.joinToString(";") { "${it.type.id}:${it.isEnabled}:${it.isCompact}" }
        prefs.edit().putString(KEY_WIDGET_CONFIGS, serialized).apply()
    }

    private fun loadStringSet(key: String): Set<String> {
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    private fun saveStringSet(key: String, set: Set<String>) {
        prefs.edit().putStringSet(key, set).apply()
    }

    private fun loadMap(key: String): Map<String, String> {
        val serialized = prefs.getString(key, null) ?: return emptyMap()
        val result = mutableMapOf<String, String>()
        for (entry in serialized.split("|")) {
            val idx = entry.indexOf("=")
            if (idx > 0) {
                val k = entry.substring(0, idx)
                val v = entry.substring(idx + 1)
                result[k] = v
            }
        }
        return result
    }

    private fun saveMap(key: String, map: Map<String, String>) {
        val serialized = map.entries.joinToString("|") { "${it.key}=${it.value}" }
        prefs.edit().putString(key, serialized).apply()
    }

    companion object {
        private const val PREFS_NAME = "aura_launcher_preferences"
        private const val KEY_ICON_THEME = "key_icon_theme"
        private const val KEY_CUSTOM_ICON_PACK = "key_custom_icon_pack"
        private const val KEY_ICON_SHAPE = "key_icon_shape"
        private const val KEY_ICON_SCALE = "key_icon_scale"
        private const val KEY_SHOW_LABELS = "key_show_labels"
        private const val KEY_GRID_COLUMNS = "key_grid_columns"
        private const val KEY_DOCK_LIMIT = "key_dock_limit"
        private const val KEY_AMOLED_BLACK = "key_amoled_black"
        private const val KEY_SHOW_SCRUBBER = "key_show_scrubber"
        private const val KEY_AUTO_KEYBOARD = "key_auto_keyboard"
        private const val KEY_GESTURE_SWIPE_UP = "key_gesture_swipe_up"
        private const val KEY_GESTURE_SWIPE_DOWN = "key_gesture_swipe_down"
        private const val KEY_GESTURE_DOUBLE_TAP = "key_gesture_double_tap"
        private const val KEY_GESTURE_PINCH = "key_gesture_pinch"
        private const val KEY_GESTURE_LONG_PRESS = "key_gesture_long_press"
        private const val KEY_SCRATCHPAD = "key_scratchpad"
        private const val KEY_WEATHER_CITY = "key_weather_city"
        private const val KEY_WEATHER_TEMP = "key_weather_temp"
        private const val KEY_WEATHER_COND = "key_weather_cond"
        private const val KEY_WEATHER_FAHRENHEIT = "key_weather_fahrenheit"
        private const val KEY_WIDGET_CONFIGS = "key_widget_configs"
        private const val KEY_PINNED_DOCK_APPS = "key_pinned_dock_apps"
        private const val KEY_PINNED_HOME_APPS = "key_pinned_home_apps"
        private const val KEY_HIDDEN_APPS = "key_hidden_apps"
        private const val KEY_CUSTOM_LABELS = "key_custom_labels"
    }
}
