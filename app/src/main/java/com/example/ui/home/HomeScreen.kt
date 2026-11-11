package com.example.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.drawer.AppContextMenuDialog
import com.example.ui.drawer.AppDrawer
import com.example.ui.drawer.AppRenameDialog
import com.example.ui.settings.SettingsBottomSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val widgetConfigs by viewModel.widgetConfigs.collectAsState()
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val weatherInfo by viewModel.weatherInfo.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val filteredDrawerApps by viewModel.filteredDrawerApps.collectAsState()
    val hiddenApps by viewModel.hiddenAppsList.collectAsState()
    val installedIconPacks by viewModel.installedIconPacks.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isSleepOverlayActive by viewModel.isSleepOverlayActive.collectAsState()
    val contextMenuApp by viewModel.contextMenuApp.collectAsState()
    val renameDialogApp by viewModel.renameDialogApp.collectAsState()

    val backgroundColor = if (preferences.isTrueAmoledBlack) AmoledBlack else GrapheneDarkSurface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .testTag("home_screen_root")
    ) {
        // Main Home Screen Content with Gestures
        GestureDetectorBox(
            gestureSettings = preferences.gestures,
            onGesture = { action -> viewModel.handleGesture(action) },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Top Inset spacer
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                // Scrollable Widgets Area
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)
                ) {
                    val enabledWidgets = widgetConfigs.filter { it.isEnabled }.sortedBy { it.order }

                    items(enabledWidgets, key = { it.type.id }) { config ->
                        when (config.type) {
                            WidgetType.CLOCK_DATE -> {
                                ClockWidgetView(
                                    batteryPercentage = batteryInfo.percentage,
                                    isCharging = batteryInfo.isCharging,
                                    onClockClick = { viewModel.openClock() }
                                )
                            }
                            WidgetType.BATTERY_STATUS -> {
                                BatteryWidgetView(
                                    batteryInfo = batteryInfo,
                                    onOpenBatterySettings = { viewModel.openBatterySettings() },
                                    isCompact = config.isCompact
                                )
                            }
                            WidgetType.PRIVACY_WEATHER -> {
                                PrivacyWeatherWidgetView(
                                    weatherInfo = weatherInfo,
                                    onWeatherClick = { viewModel.cycleWeatherCondition() },
                                    isCompact = config.isCompact
                                )
                            }
                            WidgetType.PRIVACY_AUDIT -> {
                                PrivacyAuditWidgetView(
                                    onInspectClick = { viewModel.setSettingsOpen(true) }
                                )
                            }
                            WidgetType.QUICK_ACTIONS -> {
                                QuickActionsWidgetView(
                                    onSearchClick = { viewModel.setDrawerOpen(true) },
                                    onDrawerClick = { viewModel.setDrawerOpen(true) },
                                    onLockClick = { viewModel.setSleepOverlay(true) },
                                    onSettingsClick = { viewModel.setSettingsOpen(true) }
                                )
                            }
                            WidgetType.QUICK_SCRATCHPAD -> {
                                ScratchpadWidgetView(
                                    content = preferences.scratchpadContent,
                                    onContentChange = { text -> viewModel.updateScratchpad(text) }
                                )
                            }
                        }
                    }
                }

                // Dock Section (Bottom)
                DockView(
                    dockApps = dockApps,
                    iconTheme = preferences.iconTheme,
                    iconShape = preferences.iconShape,
                    iconScalePercent = preferences.iconScalePercent,
                    showAppLabels = preferences.showAppLabels,
                    customIconPackPkg = preferences.selectedCustomIconPackPackage,
                    iconPackManager = viewModel.iconPackManager,
                    onAppClick = { app -> viewModel.launchApp(app) },
                    onAppLongClick = { app -> viewModel.showAppContextMenu(app) },
                    onOpenDrawerClick = { viewModel.setDrawerOpen(true) },
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Navigation Bar spacer
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

        // App Drawer Layer (Full screen overlay with slide transition)
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawer(
                apps = filteredDrawerApps,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                iconTheme = preferences.iconTheme,
                iconShape = preferences.iconShape,
                iconScalePercent = preferences.iconScalePercent,
                gridColumns = preferences.gridColumns,
                showAppLabels = preferences.showAppLabels,
                showAlphabetScrubber = preferences.showAlphabetScrubber,
                customIconPackPkg = preferences.selectedCustomIconPackPackage,
                iconPackManager = viewModel.iconPackManager,
                onSearchChange = { q -> viewModel.onSearchQueryChanged(q) },
                onCategorySelect = { c -> viewModel.onCategorySelected(c) },
                onAppClick = { app -> viewModel.launchApp(app) },
                onAppLongClick = { app -> viewModel.showAppContextMenu(app) },
                onCloseDrawer = { viewModel.setDrawerOpen(false) }
            )
        }

        // Sleep / Screen Off Simulation Overlay (Triggered by Double Tap or Sleep Action)
        AnimatedVisibility(
            visible = isSleepOverlayActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { viewModel.setSleepOverlay(false) }
                    .testTag("sleep_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Brightness2,
                        contentDescription = "Screen Sleep",
                        tint = GrapheneTextTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Aura Standby",
                        style = AppTypography.headlineSmall,
                        color = GrapheneTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap anywhere to wake",
                        style = AppTypography.bodySmall,
                        color = GrapheneTextTertiary
                    )
                }
            }
        }

        // Context Menu Dialog
        contextMenuApp?.let { app ->
            AppContextMenuDialog(
                app = app,
                iconTheme = preferences.iconTheme,
                iconShape = preferences.iconShape,
                iconPackManager = viewModel.iconPackManager,
                customIconPackPkg = preferences.selectedCustomIconPackPackage,
                onDismiss = { viewModel.showAppContextMenu(null) },
                onLaunch = { viewModel.launchApp(app) },
                onTogglePinDock = { viewModel.togglePinDock(app.packageName) },
                onTogglePinHome = { viewModel.togglePinHome(app.packageName) },
                onToggleHide = { viewModel.toggleHideApp(app.packageName) },
                onRename = { viewModel.showRenameDialog(app) },
                onAppInfo = { viewModel.openAppDetails(app.packageName) },
                onUninstall = { viewModel.uninstallApp(app.packageName) }
            )
        }

        // Rename Dialog
        renameDialogApp?.let { app ->
            AppRenameDialog(
                app = app,
                onDismiss = { viewModel.showRenameDialog(null) },
                onSave = { newName -> viewModel.setCustomLabel(app.packageName, newName) }
            )
        }

        // Settings Bottom Sheet
        if (isSettingsOpen) {
            SettingsBottomSheet(
                preferences = preferences,
                widgetConfigs = widgetConfigs,
                installedIconPacks = installedIconPacks,
                hiddenApps = hiddenApps,
                onDismiss = { viewModel.setSettingsOpen(false) },
                onUpdatePreferences = { update -> viewModel.updatePreferences(update) },
                onUpdateWidgetConfig = { type, enabled, compact ->
                    viewModel.updateWidgetConfig(type, enabled, compact)
                },
                onReorderWidgets = { list -> viewModel.reorderWidgets(list) },
                onUnhideApp = { pkg -> viewModel.toggleHideApp(pkg) }
            )
        }
    }
}
