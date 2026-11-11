package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PrivacyAuditWidgetView(
    onInspectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    WidgetContainer(
        modifier = modifier.testTag("privacy_audit_widget"),
        onClick = onInspectClick,
        testTag = "privacy_audit_container"
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
                    imageVector = Icons.Default.Security,
                    contentDescription = "Privacy Shield",
                    tint = GrapheneSuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Privacy Shield Status",
                    style = AppTypography.titleMedium,
                    color = GrapheneTextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GrapheneSuccessGreen.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "VERIFIED",
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GrapheneSuccessGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No Network",
                    tint = GrapheneTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "0 Bytes Transmitted",
                    style = AppTypography.bodySmall,
                    color = GrapheneTextSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Zero Telemetry",
                    tint = GrapheneSuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Zero Telemetry SDKs",
                    style = AppTypography.bodySmall,
                    color = GrapheneTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "No android.permission.INTERNET in manifest • 100% on-device sandbox",
            style = AppTypography.labelSmall,
            color = GrapheneTextTertiary
        )
    }
}
