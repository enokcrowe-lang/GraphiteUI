package com.example.data.model

enum class GestureTrigger(val title: String, val description: String) {
    SWIPE_UP("Swipe Up", "Swipe upwards anywhere on the home screen"),
    SWIPE_DOWN("Swipe Down", "Swipe downwards anywhere on the home screen"),
    DOUBLE_TAP("Double Tap", "Quick double tap on an empty home screen area"),
    PINCH_IN("Pinch In", "Two-finger pinch inward gesture"),
    LONG_PRESS("Long Press", "Press and hold on empty home screen area")
}

enum class GestureAction(val id: String, val title: String) {
    OPEN_APP_DRAWER("open_drawer", "Open App Drawer"),
    EXPAND_NOTIFICATIONS("expand_notifications", "Expand Notifications"),
    QUICK_SEARCH("quick_search", "Quick App Search"),
    LOCK_SCREEN("lock_screen", "Lock / Screen Off"),
    OPEN_SETTINGS("open_settings", "Open Launcher Settings"),
    TOGGLE_FLASHLIGHT("toggle_torch", "Toggle Flashlight"),
    NONE("none", "Do Nothing")
}

data class GestureSettings(
    val swipeUp: GestureAction = GestureAction.OPEN_APP_DRAWER,
    val swipeDown: GestureAction = GestureAction.EXPAND_NOTIFICATIONS,
    val doubleTap: GestureAction = GestureAction.LOCK_SCREEN,
    val pinchIn: GestureAction = GestureAction.QUICK_SEARCH,
    val longPress: GestureAction = GestureAction.OPEN_SETTINGS
)

data class LauncherPreferences(
    val iconTheme: BuiltInIconTheme = BuiltInIconTheme.GRAPHENE_MONOCHROME,
    val selectedCustomIconPackPackage: String? = null,
    val iconShape: IconShape = IconShape.SQUIRCLE,
    val iconScalePercent: Int = 100, // 70 to 130
    val showAppLabels: Boolean = true,
    val gridColumns: Int = 4, // 3, 4, 5
    val dockAppLimit: Int = 5, // 0 to 6
    val isTrueAmoledBlack: Boolean = true,
    val showAlphabetScrubber: Boolean = true,
    val autoOpenKeyboardInDrawer: Boolean = false,
    val gestures: GestureSettings = GestureSettings(),
    val scratchpadContent: String = "Private note — stored locally on-device. Zero telemetry.",
    val weatherCity: String = "Privacy Zone",
    val weatherTempCelsius: Int = 22,
    val weatherCondition: WeatherCondition = WeatherCondition.CLEAR_SKY,
    val weatherUseFahrenheit: Boolean = false,
    val isStealthLockActive: Boolean = false
)
