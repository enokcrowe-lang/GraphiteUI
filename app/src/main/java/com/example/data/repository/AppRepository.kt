package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.data.model.AppCategory
import com.example.data.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val preferencesRepository: LauncherPreferencesRepository
) {
    private val packageManager: PackageManager = context.packageManager
    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private var cachedRawApps: List<RawAppInfo> = emptyList()

    private data class RawAppInfo(
        val packageName: String,
        val activityName: String,
        val defaultLabel: String,
        val isSystemApp: Boolean
    )

    suspend fun loadApps(): List<AppItem> = withContext(Dispatchers.IO) {
        if (cachedRawApps.isEmpty()) {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)
            val currentPkg = context.packageName

            cachedRawApps = resolveInfos
                .filter { it.activityInfo.packageName != currentPkg }
                .map { ri ->
                    val appInfo = ri.activityInfo.applicationInfo
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    RawAppInfo(
                        packageName = ri.activityInfo.packageName,
                        activityName = ri.activityInfo.name,
                        defaultLabel = ri.loadLabel(packageManager).toString(),
                        isSystemApp = isSystem
                    )
                }
                .sortedBy { it.defaultLabel.lowercase() }
        }

        val pinnedDock = preferencesRepository.pinnedDockApps.value
        val pinnedHome = preferencesRepository.pinnedHomeApps.value
        val hidden = preferencesRepository.hiddenApps.value
        val customLabels = preferencesRepository.customLabels.value

        val items = cachedRawApps.map { raw ->
            val icon = try {
                packageManager.getActivityIcon(
                    android.content.ComponentName(raw.packageName, raw.activityName)
                )
            } catch (_: Exception) {
                try {
                    packageManager.getApplicationIcon(raw.packageName)
                } catch (_: Exception) {
                    null
                }
            }

            val category = inferCategory(raw.packageName, raw.isSystemApp)

            AppItem(
                packageName = raw.packageName,
                activityName = raw.activityName,
                defaultLabel = raw.defaultLabel,
                customLabel = customLabels[raw.packageName],
                icon = icon,
                isPinnedHome = pinnedHome.contains(raw.packageName),
                isPinnedDock = pinnedDock.contains(raw.packageName),
                isHidden = hidden.contains(raw.packageName),
                category = category
            )
        }

        _installedApps.value = items
        items
    }

    fun invalidateCache() {
        cachedRawApps = emptyList()
    }

    private fun inferCategory(packageName: String, isSystemApp: Boolean): AppCategory {
        val lower = packageName.lowercase()
        return when {
            lower.contains("dialer") || lower.contains("phone") || lower.contains("message") ||
                    lower.contains("sms") || lower.contains("contact") || lower.contains("chat") ||
                    lower.contains("telecom") || lower.contains("whatsapp") || lower.contains("signal") ||
                    lower.contains("telegram") -> AppCategory.COMMUNICATION

            lower.contains("calendar") || lower.contains("note") || lower.contains("mail") ||
                    lower.contains("task") || lower.contains("office") || lower.contains("document") ||
                    lower.contains("editor") || lower.contains("keep") -> AppCategory.PRODUCTIVITY

            lower.contains("camera") || lower.contains("gallery") || lower.contains("photo") ||
                    lower.contains("music") || lower.contains("media") || lower.contains("audio") ||
                    lower.contains("video") || lower.contains("calculator") || lower.contains("clock") ||
                    lower.contains("browser") || lower.contains("chrome") -> AppCategory.TOOLS

            isSystemApp || lower.contains("android") || lower.contains("settings") -> AppCategory.SYSTEM

            else -> AppCategory.ALL
        }
    }
}
