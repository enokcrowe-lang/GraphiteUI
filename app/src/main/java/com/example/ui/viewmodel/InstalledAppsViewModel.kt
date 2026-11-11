package com.example.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppCategory
import com.example.data.model.AppItem
import com.example.data.repository.InstalledAppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InstalledAppsUiState(
    val isLoading: Boolean = true,
    val apps: List<AppItem> = emptyList(),
    val totalCount: Int = 0,
    val searchQuery: String = "",
    val selectedCategory: AppCategory = AppCategory.ALL,
    val pinnedDockApps: List<AppItem> = emptyList(),
    val pinnedHomeApps: List<AppItem> = emptyList(),
    val hiddenApps: List<AppItem> = emptyList()
)

class InstalledAppsViewModel(
    application: Application,
    private val repository: InstalledAppRepository = InstalledAppRepository(application.applicationContext)
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    private val _isLoading = MutableStateFlow(true)

    val visibleApps: StateFlow<List<AppItem>> = repository.visibleAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedDockApps: StateFlow<List<AppItem>> = repository.pinnedDockAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedHomeApps: StateFlow<List<AppItem>> = repository.pinnedHomeAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenApps: StateFlow<List<AppItem>> = repository.hiddenAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApps: Flow<List<AppItem>> = combine(
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
    }

    val uiState: StateFlow<InstalledAppsUiState> = combine(
        filteredApps,
        pinnedDockApps,
        pinnedHomeApps,
        hiddenApps,
        _isLoading
    ) { filtered, dock, home, hidden, loading ->
        InstalledAppsUiState(
            isLoading = loading,
            apps = filtered,
            totalCount = filtered.size,
            searchQuery = _searchQuery.value,
            selectedCategory = _selectedCategory.value,
            pinnedDockApps = dock,
            pinnedHomeApps = home,
            hiddenApps = hidden
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        InstalledAppsUiState()
    )

    init {
        refreshInstalledApps()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.syncInstalledAppsFromDevice()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun setCustomLabel(packageName: String, customLabel: String) {
        viewModelScope.launch {
            repository.setCustomLabel(packageName, customLabel)
        }
    }

    fun togglePinDock(packageName: String) {
        viewModelScope.launch {
            repository.togglePinDock(packageName)
        }
    }

    fun togglePinHome(packageName: String) {
        viewModelScope.launch {
            repository.togglePinHome(packageName)
        }
    }

    fun toggleHideApp(packageName: String) {
        viewModelScope.launch {
            repository.toggleHideApp(packageName)
        }
    }

    fun launchApp(context: Context, app: AppItem) {
        viewModelScope.launch {
            repository.recordAppLaunch(app.packageName)
        }

        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                }
            } catch (_: Exception) {}
        }
    }

    fun openAppDetails(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
        }

    fun uninstallApp(context: Context, packageName: String) {
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
}
