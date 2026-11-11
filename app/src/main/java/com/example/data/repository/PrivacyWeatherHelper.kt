package com.example.data.repository

import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherInfo
import java.util.Calendar
import java.util.TimeZone

class PrivacyWeatherHelper {

    /**
     * Generates a local, privacy-preserving weather estimate based solely on
     * local device time, month of year, and user-specified location label.
     * ZERO network requests, ZERO GPS location permissions required.
     */
    fun getPrivacyWeather(
        cityName: String,
        manualTemp: Int?,
        manualCondition: WeatherCondition?,
        useFahrenheit: Boolean
    ): WeatherInfo {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val month = calendar.get(Calendar.MONTH) // 0 - 11

        // Deterministic, offline seasonal & diurnal variation
        val baseTemp = when (month) {
            in 5..8 -> 24 // Summer
            in 2..4, in 9..10 -> 18 // Spring/Autumn
            else -> 8 // Winter
        }

        val diurnalDelta = when (hour) {
            in 6..11 -> 2
            in 12..16 -> 6
            in 17..20 -> 1
            else -> -4
        }

        val resolvedTemp = manualTemp ?: (baseTemp + diurnalDelta)
        val resolvedCondition = manualCondition ?: when {
            hour in 20..23 || hour in 0..5 -> WeatherCondition.CLEAR_SKY
            hour in 14..16 -> WeatherCondition.FEW_CLOUDS
            month in 3..4 -> WeatherCondition.RAIN
            else -> WeatherCondition.CLEAR_SKY
        }

        val displayTemp = if (useFahrenheit) {
            (resolvedTemp * 9 / 5) + 32
        } else {
            resolvedTemp
        }

        val humidity = when (resolvedCondition) {
            WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> 82
            WeatherCondition.CLOUDY, WeatherCondition.MIST -> 68
            else -> 42
        }

        val wind = when (resolvedCondition) {
            WeatherCondition.THUNDERSTORM, WeatherCondition.HEAVY_RAIN -> 28
            WeatherCondition.CLOUDY -> 18
            else -> 10
        }

        return WeatherInfo(
            cityName = cityName.ifBlank { "Private Zone" },
            temperatureCelsius = displayTemp,
            condition = resolvedCondition,
            humidityPercent = humidity,
            windSpeedKmh = wind,
            isFahrenheit = useFahrenheit,
            lastUpdatedText = "On-device • Zero Tracking"
        )
    }
}
