package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun QuickActionsWidgetView(
    onSearchClick: () -> Unit,
    onDrawerClick: () -> Unit,
    onLockClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_actions_widget"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.Search,
            label = "Search",
            onClick = onSearchClick,
            modifier = Modifier.weight(1f),
            testTag = "action_search"
        )
        QuickActionButton(
            icon = Icons.Default.Apps,
            label = "Drawer",
            onClick = onDrawerClick,
            modifier = Modifier.weight(1f),
            testTag = "action_drawer"
        )
        QuickActionButton(
            icon = Icons.Default.Lock,
            label = "Sleep",
            onClick = onLockClick,
            modifier = Modifier.weight(1f),
            testTag = "action_sleep"
        )
        QuickActionButton(
            icon = Icons.Default.Tune,
            label = "Settings",
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f),
            testTag = "action_settings"
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = GrapheneDarkCard.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GrapheneBorder),
        modifier = modifier
            .height(54.dp)
            .testTag(testTag)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GrapheneTextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = AppTypography.labelSmall,
                color = GrapheneTextSecondary
            )
        }
    }
}
