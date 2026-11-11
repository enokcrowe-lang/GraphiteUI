package com.example.ui.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppItem
import com.example.data.model.BuiltInIconTheme
import com.example.data.model.IconShape
import com.example.data.repository.IconPackManager
import com.example.ui.components.AppIconView
import com.example.ui.theme.*

@Composable
fun AppContextMenuDialog(
    app: AppItem,
    iconTheme: BuiltInIconTheme,
    iconShape: IconShape,
    iconPackManager: IconPackManager,
    customIconPackPkg: String?,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onTogglePinDock: () -> Unit,
    onTogglePinHome: () -> Unit,
    onToggleHide: () -> Unit,
    onRename: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GrapheneDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, GrapheneBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_context_menu_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with App Icon and Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppIconView(
                        app = app,
                        iconTheme = iconTheme,
                        iconShape = iconShape,
                        iconPackManager = iconPackManager,
                        size = 52.dp,
                        customIconPackPkg = customIconPackPkg
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.displayLabel,
                            style = AppTypography.titleLarge,
                            color = GrapheneTextPrimary
                        )
                        Text(
                            text = app.packageName,
                            style = AppTypography.labelSmall,
                            color = GrapheneTextTertiary,
                            maxLines = 1
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = GrapheneBorder
                )

                // Menu items
                MenuItemRow(
                    icon = Icons.Default.PlayArrow,
                    label = "Launch Application",
                    onClick = {
                        onDismiss()
                        onLaunch()
                    },
                    testTag = "menu_launch"
                )

                MenuItemRow(
                    icon = if (app.isPinnedDock) Icons.Default.Star else Icons.Default.StarBorder,
                    label = if (app.isPinnedDock) "Unpin from Dock" else "Pin to Dock",
                    onClick = {
                        onDismiss()
                        onTogglePinDock()
                    },
                    testTag = "menu_pin_dock"
                )

                MenuItemRow(
                    icon = if (app.isPinnedHome) Icons.Default.PushPin else Icons.Default.PushPin,
                    label = if (app.isPinnedHome) "Unpin from Home" else "Pin to Home",
                    onClick = {
                        onDismiss()
                        onTogglePinHome()
                    },
                    testTag = "menu_pin_home"
                )

                MenuItemRow(
                    icon = Icons.Default.Edit,
                    label = "Rename / Custom Label",
                    onClick = {
                        onDismiss()
                        onRename()
                    },
                    testTag = "menu_rename"
                )

                MenuItemRow(
                    icon = if (app.isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = if (app.isHidden) "Unhide Application" else "Hide Application (Privacy)",
                    onClick = {
                        onDismiss()
                        onToggleHide()
                    },
                    testTag = "menu_hide"
                )

                MenuItemRow(
                    icon = Icons.Default.Info,
                    label = "Application Details & Permissions",
                    onClick = {
                        onDismiss()
                        onAppInfo()
                    },
                    testTag = "menu_app_info"
                )

                MenuItemRow(
                    icon = Icons.Default.Delete,
                    label = "Uninstall",
                    color = GrapheneErrorRed,
                    onClick = {
                        onDismiss()
                        onUninstall()
                    },
                    testTag = "menu_uninstall"
                )
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    color: androidx.compose.ui.graphics.Color = GrapheneTextPrimary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = AppTypography.bodyLarge,
            color = color
        )
    }
}
