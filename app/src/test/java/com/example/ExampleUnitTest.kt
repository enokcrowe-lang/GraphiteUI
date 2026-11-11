package com.example

import com.example.data.database.InstalledAppEntity
import com.example.data.model.*
import com.example.data.repository.PrivacyWeatherHelper
import com.example.ui.viewmodel.InstalledAppsUiState
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `installed app entity correctly provides display label and component key`() {
        val entityWithoutCustom = InstalledAppEntity(
            packageName = "org.grapheneos.vanadium",
            activityName = "org.chromium.chrome.browser.ChromeTabbedActivity",
            defaultLabel = "Vanadium",
            customLabel = null
        )
        assertEquals("Vanadium", entityWithoutCustom.displayLabel)
        assertEquals("org.grapheneos.vanadium/org.chromium.chrome.browser.ChromeTabbedActivity", entityWithoutCustom.componentKey)

        val entityWithCustom = entityWithoutCustom.copy(customLabel = "Secure Browser")
        assertEquals("Secure Browser", entityWithCustom.displayLabel)
    }

    @Test
    fun `installed apps ui state default values are clean`() {
        val state = InstalledAppsUiState()
        assertTrue(state.isLoading)
        assertTrue(state.apps.isEmpty())
        assertEquals(0, state.totalCount)
        assertEquals("", state.searchQuery)
        assertEquals(AppCategory.ALL, state.selectedCategory)
    }

    @Test
    fun `privacy weather helper produces offline deterministic weather without network`() {
        val helper = PrivacyWeatherHelper()
        val weather = helper.getPrivacyWeather(
            cityName = "Privacy Haven",
            manualTemp = 20,
            manualCondition = WeatherCondition.CLEAR_SKY,
            useFahrenheit = false
        )

        assertEquals("Privacy Haven", weather.cityName)
        assertEquals(20, weather.temperatureCelsius)
        assertEquals(WeatherCondition.CLEAR_SKY, weather.condition)
        assertFalse(weather.isFahrenheit)
        assertTrue(weather.lastUpdatedText.contains("Zero Tracking"))
    }

    @Test
    fun `privacy weather fahrenheit conversion is accurate`() {
        val helper = PrivacyWeatherHelper()
        val weatherF = helper.getPrivacyWeather(
            cityName = "Local",
            manualTemp = 20,
            manualCondition = WeatherCondition.FEW_CLOUDS,
            useFahrenheit = true
        )

        // 20 * 9/5 + 32 = 68°F
        assertEquals(68, weatherF.temperatureCelsius)
        assertTrue(weatherF.isFahrenheit)
    }

    @Test
    fun `app item component key and display label format correctly`() {
        val app = AppItem(
            packageName = "org.grapheneos.camera",
            activityName = "org.grapheneos.camera.CameraActivity",
            defaultLabel = "Camera",
            customLabel = "Secure Cam",
            launchCount = 5
        )

        assertEquals("org.grapheneos.camera/org.grapheneos.camera.CameraActivity", app.componentKey)
        assertEquals("Secure Cam", app.displayLabel)
        assertEquals(5, app.launchCount)
    }

    @Test
    fun `gesture settings default to GrapheneOS minimalist actions`() {
        val gestures = GestureSettings()
        assertEquals(GestureAction.OPEN_APP_DRAWER, gestures.swipeUp)
        assertEquals(GestureAction.EXPAND_NOTIFICATIONS, gestures.swipeDown)
        assertEquals(GestureAction.LOCK_SCREEN, gestures.doubleTap)
        assertEquals(GestureAction.QUICK_SEARCH, gestures.pinchIn)
        assertEquals(GestureAction.OPEN_SETTINGS, gestures.longPress)
    }

    @Test
    fun `launcher preferences default to battery saving true amoled black`() {
        val prefs = LauncherPreferences()
        assertTrue(prefs.isTrueAmoledBlack)
        assertEquals(BuiltInIconTheme.GRAPHENE_MONOCHROME, prefs.iconTheme)
        assertEquals(IconShape.SQUIRCLE, prefs.iconShape)
        assertEquals(4, prefs.gridColumns)
    }
}
