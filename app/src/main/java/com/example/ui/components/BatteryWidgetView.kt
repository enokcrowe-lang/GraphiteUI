package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryInfo
import com.example.ui.theme.*

@Composable
fun BatteryWidgetView(
    batteryInfo: BatteryInfo,
    onOpenBatterySettings: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    WidgetContainer(
        modifier = modifier.testTag("battery_widget"),
        onClick = onOpenBatterySettings,
        testTag = "battery_widget_container"
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
                    imageVector = if (batteryInfo.isCharging) Icons.Default.Bolt else Icons.Default.BatteryChargingFull,
                    contentDescription = "Battery Status",
                    tint = if (batteryInfo.isCharging) GrapheneAccentTeal else GrapheneTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Power & Battery",
                    style = AppTypography.titleMedium,
                    color = GrapheneTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GrapheneBorder.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "${batteryInfo.percentage}%",
                    style = AppTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (batteryInfo.percentage <= 20 && !batteryInfo.isCharging) GrapheneWarningAmber else GrapheneTextPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Minimal battery progress bar
        val progress = (batteryInfo.percentage / 100f).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(GrapheneBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when {
                            batteryInfo.isCharging -> GrapheneAccentTeal
                            batteryInfo.percentage <= 20 -> GrapheneWarningAmber
                            else -> GrapheneAccentBlue
                        }
                    )
            )
        }

        if (!isCompact) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "STATUS",
                        style = AppTypography.labelSmall,
                        color = GrapheneTextTertiary
                    )
                    Text(
                        text = batteryInfo.chargeType,
                        style = AppTypography.bodySmall,
                        color = GrapheneTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TEMP",
                        style = AppTypography.labelSmall,
                        color = GrapheneTextTertiary
                    )
                    Text(
                        text = "%.1f°C".format(batteryInfo.temperatureCelsius),
                        style = AppTypography.bodySmall,
                        color = GrapheneTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "HEALTH",
                        style = AppTypography.labelSmall,
                        color = GrapheneTextTertiary
                    )
                    Text(
                        text = batteryInfo.health,
                        style = AppTypography.bodySmall,
                        color = GrapheneSuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Zero wakeups",
                    tint = GrapheneSuccessGreen,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Zero background polling • Event-driven sticky receiver",
                    style = AppTypography.labelSmall,
                    color = GrapheneTextTertiary
                )
            }
        }
    }
}
