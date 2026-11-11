package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    preferences: LauncherPreferences,
    widgetConfigs: List<WidgetConfig>,
    installedIconPacks: List<InstalledIconPack>,
    hiddenApps: List<AppItem>,
    onDismiss: () -> Unit,
    onUpdatePreferences: ((LauncherPreferences) -> LauncherPreferences) -> Unit,
    onUpdateWidgetConfig: (WidgetType, Boolean, Boolean) -> Unit,
    onReorderWidgets: (List<WidgetConfig>) -> Unit,
    onUnhideApp: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GrapheneDarkCard,
        tonalElevation = 0.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = GrapheneBorder)
        },
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Gestures", "Icon Packs", "Widgets", "Layout", "Privacy")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Aura Settings",
                        tint = GrapheneAccentTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Aura Launcher",
                        style = AppTypography.headlineSmall,
                        color = GrapheneTextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("settings_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GrapheneTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = GrapheneDarkCard,
                contentColor = GrapheneAccentTeal,
                edgePadding = 0.dp,
                divider = { HorizontalDivider(color = GrapheneBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = AppTypography.labelLarge,
                                color = if (selectedTab == index) GrapheneAccentTeal else GrapheneTextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_${title.lowercase().replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Tabs
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 60.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> {
                        // GESTURES TAB
                        item {
                            SectionHeader(title = "Gesture Controls", subtitle = "Customize swipe and tap actions on your home screen")
                        }
                        item {
                            GestureSelectorRow(
                                title = "Swipe Up",
                                currentAction = preferences.gestures.swipeUp,
                                onSelect = { act ->
                                    onUpdatePreferences { it.copy(gestures = it.gestures.copy(swipeUp = act)) }
                                }
                            )
                        }
                        item {
                            GestureSelectorRow(
                                title = "Swipe Down",
                                currentAction = preferences.gestures.swipeDown,
                                onSelect = { act ->
                                    onUpdatePreferences { it.copy(gestures = it.gestures.copy(swipeDown = act)) }
                                }
                            )
                        }
                        item {
                            GestureSelectorRow(
                                title = "Double Tap",
                                currentAction = preferences.gestures.doubleTap,
                                onSelect = { act ->
                                    onUpdatePreferences { it.copy(gestures = it.gestures.copy(doubleTap = act)) }
                                }
                            )
                        }
                        item {
                            GestureSelectorRow(
                                title = "Pinch In",
                                currentAction = preferences.gestures.pinchIn,
                                onSelect = { act ->
                                    onUpdatePreferences { it.copy(gestures = it.gestures.copy(pinchIn = act)) }
                                }
                            )
                        }
                        item {
                            GestureSelectorRow(
                                title = "Long Press",
                                currentAction = preferences.gestures.longPress,
                                onSelect = { act ->
                                    onUpdatePreferences { it.copy(gestures = it.gestures.copy(longPress = act)) }
                                }
                            )
                        }
                    }

                    1 -> {
                        // ICON PACKS & STYLING
                        item {
                            SectionHeader(title = "Custom Icon Packs & Themes", subtitle = "Choose minimalist styles or third-party installed icon packs")
                        }
                        item {
                            Text(text = "Built-in Themes", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            BuiltInIconTheme.entries.forEach { theme ->
                                val isSelected = preferences.iconTheme == theme && preferences.selectedCustomIconPackPackage == null
                                ThemeChoiceCard(
                                    title = theme.title,
                                    description = theme.description,
                                    isSelected = isSelected,
                                    onClick = {
                                        onUpdatePreferences {
                                            it.copy(iconTheme = theme, selectedCustomIconPackPackage = null)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (installedIconPacks.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Installed Icon Packs", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                installedIconPacks.forEach { pack ->
                                    val isSelected = preferences.selectedCustomIconPackPackage == pack.packageName
                                    ThemeChoiceCard(
                                        title = pack.name,
                                        description = "Package: ${pack.packageName}",
                                        isSelected = isSelected,
                                        onClick = {
                                            onUpdatePreferences {
                                                it.copy(selectedCustomIconPackPackage = pack.packageName)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Icon Shape Mask", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconShape.entries.forEach { shape ->
                                    val isSel = preferences.iconShape == shape
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSel) GrapheneAccentTeal else GrapheneDarkSurface,
                                        border = BorderStroke(1.dp, if (isSel) GrapheneAccentTeal else GrapheneBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onUpdatePreferences { it.copy(iconShape = shape) }
                                            }
                                    ) {
                                        Text(
                                            text = shape.title,
                                            style = AppTypography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                            color = if (isSel) AmoledBlack else GrapheneTextSecondary,
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Icon Size", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                                Text(text = "${preferences.iconScalePercent}%", style = AppTypography.labelLarge, color = GrapheneAccentTeal)
                            }
                            Slider(
                                value = preferences.iconScalePercent.toFloat(),
                                onValueChange = { scale ->
                                    onUpdatePreferences { it.copy(iconScalePercent = scale.toInt()) }
                                },
                                valueRange = 70f..130f,
                                steps = 11,
                                colors = SliderDefaults.colors(
                                    thumbColor = GrapheneAccentTeal,
                                    activeTrackColor = GrapheneAccentTeal,
                                    inactiveTrackColor = GrapheneBorder
                                ),
                                modifier = Modifier.testTag("slider_icon_size")
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Show Application Labels", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                                    Text(text = "Toggle text labels beneath icons", style = AppTypography.bodySmall, color = GrapheneTextTertiary)
                                }
                                Switch(
                                    checked = preferences.showAppLabels,
                                    onCheckedChange = { chk ->
                                        onUpdatePreferences { it.copy(showAppLabels = chk) }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AmoledBlack,
                                        checkedTrackColor = GrapheneAccentTeal
                                    ),
                                    modifier = Modifier.testTag("switch_show_labels")
                                )
                            }
                        }
                    }

                    2 -> {
                        // WIDGETS MANAGER
                        item {
                            SectionHeader(title = "Home Screen Widget System", subtitle = "Toggle, configure and order local minimalist widgets")
                        }
                        items(widgetConfigs.size) { idx ->
                            val config = widgetConfigs[idx]
                            WidgetConfigRow(
                                config = config,
                                onToggle = { enabled ->
                                    onUpdateWidgetConfig(config.type, enabled, config.isCompact)
                                },
                                onToggleCompact = { compact ->
                                    onUpdateWidgetConfig(config.type, config.isEnabled, compact)
                                },
                                onMoveUp = if (idx > 0) {
                                    {
                                        val mutable = widgetConfigs.toMutableList()
                                        val temp = mutable[idx]
                                        mutable[idx] = mutable[idx - 1]
                                        mutable[idx - 1] = temp
                                        onReorderWidgets(mutable)
                                    }
                                } else null,
                                onMoveDown = if (idx < widgetConfigs.size - 1) {
                                    {
                                        val mutable = widgetConfigs.toMutableList()
                                        val temp = mutable[idx]
                                        mutable[idx] = mutable[idx + 1]
                                        mutable[idx + 1] = temp
                                        onReorderWidgets(mutable)
                                    }
                                } else null
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionHeader(title = "Privacy Weather Preferences", subtitle = "Local zero-tracking settings")
                            OutlinedTextField(
                                value = preferences.weatherCity,
                                onValueChange = { city ->
                                    onUpdatePreferences { it.copy(weatherCity = city) }
                                },
                                label = { Text("Display Location Name") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GrapheneAccentTeal,
                                    unfocusedBorderColor = GrapheneBorder,
                                    focusedTextColor = GrapheneTextPrimary,
                                    unfocusedTextColor = GrapheneTextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Use Fahrenheit (°F)", style = AppTypography.bodyMedium, color = GrapheneTextPrimary)
                                Switch(
                                    checked = preferences.weatherUseFahrenheit,
                                    onCheckedChange = { useF ->
                                        onUpdatePreferences { it.copy(weatherUseFahrenheit = useF) }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AmoledBlack,
                                        checkedTrackColor = GrapheneAccentTeal
                                    )
                                )
                            }
                        }
                    }

                    3 -> {
                        // LAYOUT & GRID
                        item {
                            SectionHeader(title = "Layout & Dock", subtitle = "Configure grid density and bottom favorites")
                        }
                        item {
                            Text(text = "Drawer Grid Columns", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(3, 4, 5).forEach { cols ->
                                    val isSel = preferences.gridColumns == cols
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSel) GrapheneAccentTeal else GrapheneDarkSurface,
                                        border = BorderStroke(1.dp, if (isSel) GrapheneAccentTeal else GrapheneBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onUpdatePreferences { it.copy(gridColumns = cols) }
                                            }
                                    ) {
                                        Text(
                                            text = "$cols Columns",
                                            style = AppTypography.labelLarge.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                            color = if (isSel) AmoledBlack else GrapheneTextSecondary,
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Pure AMOLED Black", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                                    Text(text = "True #000000 black to maximize battery savings on OLED displays", style = AppTypography.bodySmall, color = GrapheneTextTertiary)
                                }
                                Switch(
                                    checked = preferences.isTrueAmoledBlack,
                                    onCheckedChange = { isAmoled ->
                                        onUpdatePreferences { it.copy(isTrueAmoledBlack = isAmoled) }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AmoledBlack,
                                        checkedTrackColor = GrapheneAccentTeal
                                    )
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Alphabet Fast-Scrubber", style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                                    Text(text = "Quick A-Z jump bar on the right edge of app drawer", style = AppTypography.bodySmall, color = GrapheneTextTertiary)
                                }
                                Switch(
                                    checked = preferences.showAlphabetScrubber,
                                    onCheckedChange = { scrubber ->
                                        onUpdatePreferences { it.copy(showAlphabetScrubber = scrubber) }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AmoledBlack,
                                        checkedTrackColor = GrapheneAccentTeal
                                    )
                                )
                            }
                        }

                        if (hiddenApps.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                SectionHeader(title = "Hidden Apps (${hiddenApps.size})", subtitle = "Applications hidden from home and drawer")
                                hiddenApps.forEach { app ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = app.displayLabel, style = AppTypography.bodyMedium, color = GrapheneTextPrimary)
                                        TextButton(onClick = { onUnhideApp(app.packageName) }) {
                                            Text("Unhide", color = GrapheneAccentTeal)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // PRIVACY & AUDIT
                        item {
                            SectionHeader(
                                title = "Privacy & Zero-Telemetry Guarantee",
                                subtitle = "Architectural audit of network, telemetry, and background services"
                            )
                        }
                        item {
                            PrivacyAuditCard(
                                icon = Icons.Default.WifiOff,
                                title = "Zero Network Permissions",
                                description = "AndroidManifest.xml declares zero internet access. The Android OS sandbox physically prevents any network packets from being sent or received."
                            )
                        }
                        item {
                            PrivacyAuditCard(
                                icon = Icons.Default.Shield,
                                title = "No Telemetry or Tracking SDKs",
                                description = "Zero analytics libraries (no Firebase Analytics, no AdMob, no Google Play Services trackers, no Crashlytics). 0 bytes transmitted."
                            )
                        }
                        item {
                            PrivacyAuditCard(
                                icon = Icons.Default.BatterySaver,
                                title = "Ultra-Low Power & Zero Daemons",
                                description = "Zero persistent background services, zero WakeLocks, zero alarms. State updates are strictly event-driven (sticky broadcasts & UI lifecycle)."
                            )
                        }
                        item {
                            PrivacyAuditCard(
                                icon = Icons.Default.Code,
                                title = "100% Offline & Open-Source",
                                description = "Licensed under MIT License. All data (pinned apps, notes, gestures, settings) resides exclusively on your local device."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(text = title, style = AppTypography.titleLarge, color = GrapheneTextPrimary)
        Text(text = subtitle, style = AppTypography.bodySmall, color = GrapheneTextTertiary)
    }
}

@Composable
private fun GestureSelectorRow(
    title: String,
    currentAction: GestureAction,
    onSelect: (GestureAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GrapheneDarkSurface,
        border = BorderStroke(1.dp, GrapheneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                Text(text = currentAction.title, style = AppTypography.bodySmall, color = GrapheneAccentTeal)
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint = GrapheneTextSecondary
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(GrapheneDarkCard)
            ) {
                GestureAction.entries.forEach { action ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = action.title,
                                color = if (action == currentAction) GrapheneAccentTeal else GrapheneTextPrimary
                            )
                        },
                        onClick = {
                            onSelect(action)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeChoiceCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) GrapheneAccentTeal.copy(alpha = 0.15f) else GrapheneDarkSurface,
        border = BorderStroke(1.dp, if (isSelected) GrapheneAccentTeal else GrapheneBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.titleMedium,
                    color = if (isSelected) GrapheneAccentTeal else GrapheneTextPrimary
                )
                Text(
                    text = description,
                    style = AppTypography.bodySmall,
                    color = GrapheneTextSecondary
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = GrapheneAccentTeal
                )
            }
        }
    }
}

@Composable
private fun WidgetConfigRow(
    config: WidgetConfig,
    onToggle: (Boolean) -> Unit,
    onToggleCompact: (Boolean) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GrapheneDarkSurface,
        border = BorderStroke(1.dp, GrapheneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = config.type.title, style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                    Text(text = config.type.description, style = AppTypography.bodySmall, color = GrapheneTextTertiary)
                }
                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AmoledBlack,
                        checkedTrackColor = GrapheneAccentTeal
                    )
                )
            }

            if (config.isEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onMoveUp ?: {}, enabled = onMoveUp != null) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move up", tint = if (onMoveUp != null) GrapheneTextPrimary else GrapheneTextTertiary)
                        }
                        IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move down", tint = if (onMoveDown != null) GrapheneTextPrimary else GrapheneTextTertiary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Compact", style = AppTypography.labelSmall, color = GrapheneTextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Checkbox(
                            checked = config.isCompact,
                            onCheckedChange = onToggleCompact,
                            colors = CheckboxDefaults.colors(checkedColor = GrapheneAccentTeal)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyAuditCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GrapheneDarkSurface,
        border = BorderStroke(1.dp, GrapheneBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GrapheneAccentTeal,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(text = title, style = AppTypography.titleMedium, color = GrapheneTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = AppTypography.bodySmall, color = GrapheneTextSecondary)
            }
        }
    }
}
