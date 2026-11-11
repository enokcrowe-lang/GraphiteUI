package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockWidgetView(
    batteryPercentage: Int,
    isCharging: Boolean,
    onClockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    // Minimal local minute tick (event loop sleeps for ~10 seconds)
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(15000L)
        }
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("clock_widget")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClockClick
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Shield",
                tint = GrapheneAccentTeal,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "OFFLINE • SECURE",
                style = AppTypography.labelSmall,
                color = GrapheneAccentTeal,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = timeFormat.format(currentTime),
            style = AppTypography.displayLarge.copy(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 58.sp,
                lineHeight = 62.sp
            ),
            color = GrapheneTextPrimary
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = dateFormat.format(currentTime),
                style = AppTypography.titleMedium,
                color = GrapheneTextSecondary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isCharging) "⚡ $batteryPercentage%" else "$batteryPercentage%",
                    style = AppTypography.labelSmall,
                    color = if (batteryPercentage <= 20 && !isCharging) GrapheneWarningAmber else GrapheneTextTertiary
                )
            }
        }
    }
}
