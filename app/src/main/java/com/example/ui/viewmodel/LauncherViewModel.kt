package com.example.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    val preferencesRepository = LauncherPreferencesRepository(context)
    val installedAppRepository = InstalledAppRepository(context)
    val iconPackManager = IconPackManager(context)
    val batteryMonitor = BatteryMonitor(context)
    val weatherHelper = PrivacyWeatherHelper()

    val preferences = preferencesRepository.preferences
    val widgetConfigs = preferencesRepository.widgetConfigs
    val batteryInfo = batteryMonitor.batteryInfo

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen = _isDrawerOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen = _isSettingsOpen.asStateFlow()

    private val _isSleepOverlayActive = MutableStateFlow(false)
    val isSleepOverlayActive = _isSleepOverlayActive.asStateFlow()

    private val _contextMenuApp = MutableStateFlow<AppItem?>(null)
    val contextMenuApp = _contextMenuApp.asStateFlow()

    private val _renameDialogApp = MutableStateFlow<AppItem?>(null)
    val renameDialogApp = _renameDialogApp.asStateFlow()

    private val _installedIconPacks = MutableStateFlow<List<InstalledIconPack>>(emptyList())
    val installedIconPacks = _installedIconPacks.asStateFlow()

    private val _homePage = MutableStateFlow(0)
    val homePage = _homePage.asStateFlow()

    // Room-backed reactive app flows
    val visibleApps: StateFlow<List<AppItem>> = installedAppRepository.visibleAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dockApps: StateFlow<List<AppItem>> = combine(
        installedAppRepository.pinnedDockAppsFlow,
        preferences
    ) { apps, prefs ->
        apps.take(prefs.dockAppLimit)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedHomeApps: StateFlow<List<AppItem>> = installedAppRepository.pinnedHomeAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenAppsList: StateFlow<List<AppItem>> = installedAppRepository.hiddenAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weatherInfo: StateFlow<WeatherInfo> = combine(
        preferences,
        _searchQuery
    ) { prefs, _ ->
        weatherHelper.getPrivacyWeather(
            cityName = prefs.weatherCity,
            manualTemp = prefs.weatherTempCelsius,
            manualCondition = prefs.weatherCondition,
            useFahrenheit = prefs.weatherUseFahrenheit
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        weatherHelper.getPrivacyWeather("Local Zone", 22, WeatherCondition.CLEAR_SKY, false)
    )

    // Filtered drawer apps from Room database
    val filteredDrawerApps: StateFlow<List<AppItem>> = combine(
        visibleApps,
        _searchQuery,
        _selectedCategory
    ) { apps, query, category ->
        apps.filter { app ->
            val matchesCategory = category == AppCategory.ALL || app.category == category
            val matchesQuery = query.isBlank() ||
                    app.displayLabel.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        try {
            loadData()
            batteryMonitor.startListening()
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        try {
            batteryMonitor.stopListening()
        } catch (_: Exception) {}
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                installedAppRepository.syncInstalledAppsFromDevice()
            } catch (_: Exception) {}
            try {
                _installedIconPacks.value = iconPackManager.getInstalledIconPacks()
            } catch (_: Exception) {}
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun setDrawerOpen(isOpen: Boolean) {
        _isDrawerOpen.value = isOpen
        if (!isOpen) {
            _searchQuery.value = ""
        }
    }

    fun setSettingsOpen(isOpen: Boolean) {
        _isSettingsOpen.value = isOpen
    }

    fun setSleepOverlay(active: Boolean) {
        _isSleepOverlayActive.value = active
    }

    fun showAppContextMenu(app: AppItem?) {
        _contextMenuApp.value = app
    }

    fun showRenameDialog(app: AppItem?) {
        _renameDialogApp.value = app
    }

    fun setHomePage(page: Int) {
        _homePage.value = page
    }

    fun launchApp(app: AppItem) {
        viewModelScope.launch {
            try {
                installedAppRepository.recordAppLaunch(app.packageName)
            } catch (_: Exception) {}
        }

        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
            setDrawerOpen(false)
        } catch (_: Exception) {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    setDrawerOpen(false)
                }
            } catch (_: Exception) {}
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = Uri.parse("package:$packageName")
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_DELETE).apply {
                    data = Uri.parse("package:$packageName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openClock() {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openBatterySettings() {
        try {
            val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun expandNotificationShade() {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManagerClass = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManagerClass.getMethod("expandNotificationsPanel")
            expand.invoke(statusBarService)
        } catch (_: Exception) {
            setDrawerOpen(true)
        }
    }

    fun handleGesture(action: GestureAction) {
        when (action) {
            GestureAction.OPEN_APP_DRAWER -> setDrawerOpen(true)
            GestureAction.EXPAND_NOTIFICATIONS -> expandNotificationShade()
            GestureAction.QUICK_SEARCH -> setDrawerOpen(true)
            GestureAction.LOCK_SCREEN -> setSleepOverlay(true)
            GestureAction.OPEN_SETTINGS -> setSettingsOpen(true)
            GestureAction.TOGGLE_FLASHLIGHT -> Unit
            GestureAction.NONE -> Unit
        }
    }

    fun togglePinDock(packageName: String) {
        viewModelScope.launch {
            try {
                installedAppRepository.togglePinDock(packageName)
            } catch (_: Exception) {}
        }
    }

    fun togglePinHome(packageName: String) {
        viewModelScope.launch {
            try {
                installedAppRepository.togglePinHome(packageName)
            } catch (_: Exception) {}
        }
    }

    fun toggleHideApp(packageName: String) {
        viewModelScope.launch {
            try {
                installedAppRepository.toggleHideApp(packageName)
            } catch (_: Exception) {}
        }
    }

    fun setCustomLabel(packageName: String, label: String) {
        viewModelScope.launch {
            try {
                installedAppRepository.setCustomLabel(packageName, label)
            } catch (_: Exception) {}
        }
    }

    fun updatePreferences(transform: (LauncherPreferences) -> LauncherPreferences) {
        preferencesRepository.updatePreferences(transform)
    }

    fun updateWidgetConfig(type: WidgetType, isEnabled: Boolean, isCompact: Boolean) {
        preferencesRepository.updateWidgetConfig(type, isEnabled, isCompact)
    }

    fun reorderWidgets(newList: List<WidgetConfig>) {
        preferencesRepository.reorderWidgets(newList)
    }

    fun updateScratchpad(text: String) {
        preferencesRepository.updateScratchpad(text)
    }

    fun cycleWeatherCondition() {
        val all = WeatherCondition.entries
        val nextIdx = (preferences.value.weatherCondition.ordinal + 1) % all.size
        updatePreferences { it.copy(weatherCondition = all[nextIdx]) }
    }

    fun setWeatherCity(name: String) {
        updatePreferences { it.copy(weatherCity = name) }
    }

    fun setWeatherTemp(celsius: Int) {
        updatePreferences { it.copy(weatherTempCelsius = celsius) }
    }

    fun toggleWeatherUnit() {
        updatePreferences { it.copy(weatherUseFahrenheit = !it.weatherUseFahrenheit) }
    }
}
