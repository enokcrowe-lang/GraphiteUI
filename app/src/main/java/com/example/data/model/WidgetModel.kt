package com.example.data.model

enum class WidgetType(val id: String, val title: String, val description: String) {
    CLOCK_DATE("clock_date", "Minimalist Clock & Date", "Clean typography time, day, date, and stealth battery status"),
    BATTERY_STATUS("battery_status", "Battery & Power Monitor", "Hardware battery %, temperature, health, and low-power telemetry audit"),
    PRIVACY_WEATHER("privacy_weather", "Privacy Weather", "Local opt-in weather with zero continuous location tracking or analytics"),
    PRIVACY_AUDIT("privacy_audit", "Privacy Shield & Telemetry", "Real-time verification of 0 bytes network sent and sandbox isolation"),
    QUICK_SCRATCHPAD("quick_scratchpad", "Quick Scratchpad", "Encrypted, on-device instant notes on your home screen"),
    QUICK_ACTIONS("quick_actions", "Action Shortcuts", "Quick access pills for Search, Sleep, Sound, and Settings")
}

data class WidgetConfig(
    val type: WidgetType,
    val isEnabled: Boolean = true,
    val order: Int = 0,
    val showBorder: Boolean = true,
    val isCompact: Boolean = false
)

data class BatteryInfo(
    val percentage: Int = 100,
    val isCharging: Boolean = false,
    val chargeType: String = "Discharging",
    val health: String = "Good",
    val temperatureCelsius: Float = 25.0f,
    val voltageMilliVolts: Int = 4100
)

data class WeatherInfo(
    val cityName: String = "Local Area",
    val temperatureCelsius: Int = 21,
    val condition: WeatherCondition = WeatherCondition.CLEAR_SKY,
    val humidityPercent: Int = 45,
    val windSpeedKmh: Int = 12,
    val isFahrenheit: Boolean = false,
    val lastUpdatedText: String = "Cached locally"
)

enum class WeatherCondition(val label: String) {
    CLEAR_SKY("Clear Sky"),
    FEW_CLOUDS("Partly Cloudy"),
    CLOUDY("Overcast"),
    RAIN("Light Rain"),
    HEAVY_RAIN("Heavy Rain"),
    THUNDERSTORM("Thunderstorm"),
    SNOW("Snow"),
    MIST("Mist / Fog")
}
