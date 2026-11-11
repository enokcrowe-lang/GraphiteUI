package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherInfo
import com.example.ui.theme.*

@Composable
fun PrivacyWeatherWidgetView(
    weatherInfo: WeatherInfo,
    onWeatherClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val icon = when (weatherInfo.condition) {
        WeatherCondition.CLEAR_SKY -> Icons.Default.WbSunny
        WeatherCondition.FEW_CLOUDS -> Icons.Default.CloudQueue
        WeatherCondition.CLOUDY -> Icons.Default.Cloud
        WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> Icons.Default.WaterDrop
        WeatherCondition.THUNDERSTORM -> Icons.Default.FlashOn
        WeatherCondition.SNOW -> Icons.Default.AcUnit
        WeatherCondition.MIST -> Icons.Default.Deblur
    }

    WidgetContainer(
        modifier = modifier.testTag("weather_widget"),
        onClick = onWeatherClick,
        testTag = "weather_widget_container"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = weatherInfo.condition.label,
                    tint = if (weatherInfo.condition == WeatherCondition.CLEAR_SKY) GrapheneWarningAmber else GrapheneAccentBlue,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = weatherInfo.cityName,
                        style = AppTypography.titleMedium,
                        color = GrapheneTextPrimary
                    )
                    Text(
                        text = weatherInfo.condition.label,
                        style = AppTypography.bodySmall,
                        color = GrapheneTextSecondary
                    )
                }
            }

            Text(
                text = "${weatherInfo.temperatureCelsius}°${if (weatherInfo.isFahrenheit) "F" else "C"}",
                style = AppTypography.displayMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light
                ),
                color = GrapheneTextPrimary
            )
        }

        if (!isCompact) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Humidity",
                        tint = GrapheneTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Humidity: ${weatherInfo.humidityPercent}%",
                        style = AppTypography.bodySmall,
                        color = GrapheneTextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = "Wind",
                        tint = GrapheneTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Wind: ${weatherInfo.windSpeedKmh} km/h",
                        style = AppTypography.bodySmall,
                        color = GrapheneTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Zero Tracking",
                    tint = GrapheneAccentTeal,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Zero GPS tracking • Local offline climate engine",
                    style = AppTypography.labelSmall,
                    color = GrapheneTextTertiary
                )
            }
        }
    }
}
