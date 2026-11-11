package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AppItem
import com.example.data.model.BuiltInIconTheme
import com.example.data.model.IconShape
import com.example.data.repository.IconPackManager
import com.example.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DockView(
    dockApps: List<AppItem>,
    iconTheme: BuiltInIconTheme,
    iconShape: IconShape,
    iconScalePercent: Int,
    showAppLabels: Boolean,
    customIconPackPkg: String?,
    iconPackManager: IconPackManager,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onOpenDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GrapheneDarkSurface.copy(alpha = 0.7f),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dock_view")
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        if (dockApps.isEmpty()) {
            // Minimal drawer prompt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
                    .combinedClickable(
                        onClick = onOpenDrawerClick
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Swipe up for apps",
                    tint = GrapheneTextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Swipe up for apps • Long press to pin to dock",
                    style = AppTypography.labelSmall,
                    color = GrapheneTextTertiary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val baseSize = (46 * (iconScalePercent / 100f)).dp

                dockApps.forEach { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .testTag("dock_app_${app.packageName}")
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                            .padding(4.dp)
                    ) {
                        AppIconView(
                            app = app,
                            iconTheme = iconTheme,
                            iconShape = iconShape,
                            iconPackManager = iconPackManager,
                            size = baseSize,
                            customIconPackPkg = customIconPackPkg
                        )

                        if (showAppLabels) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = app.displayLabel,
                                style = AppTypography.bodySmall,
                                color = GrapheneTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
