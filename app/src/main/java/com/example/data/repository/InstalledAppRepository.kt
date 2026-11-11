package com.example.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import com.example.data.database.AuraDatabase
import com.example.data.database.InstalledAppDao
import com.example.data.database.InstalledAppEntity
import com.example.data.model.AppCategory
import com.example.data.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class InstalledAppRepository(
    private val context: Context,
    private val dao: InstalledAppDao = AuraDatabase.getDatabase(context).installedAppDao()
) {
    private val packageManager: PackageManager = context.packageManager
    private val iconCache = LruCache<String, Drawable>(150)

    val allAppsFlow: Flow<List<AppItem>> = dao.getAllAppsFlow().map { entities ->
        entities.map { it.toAppItem() }
    }

    val visibleAppsFlow: Flow<List<AppItem>> = dao.getVisibleAppsFlow().map { entities ->
        entities.map { it.toAppItem() }
    }

    val pinnedDockAppsFlow: Flow<List<AppItem>> = dao.getPinnedDockAppsFlow().map { entities ->
        entities.map { it.toAppItem() }
    }

    val pinnedHomeAppsFlow: Flow<List<AppItem>> = dao.getPinnedHomeAppsFlow().map { entities ->
        entities.map { it.toAppItem() }
    }

    val hiddenAppsFlow: Flow<List<AppItem>> = dao.getHiddenAppsFlow().map { entities ->
        entities.map { it.toAppItem() }
    }

    fun searchAppsFlow(query: String): Flow<List<AppItem>> = dao.searchAppsFlow(query).map { entities ->
        entities.map { it.toAppItem() }
    }

    /**
     * Scans installed launcher activities on the Android device and synchronizes them
     * into the local Room database, preserving all user customizations and pins.
     */
    suspend fun syncInstalledAppsFromDevice() = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)
        val currentPackage = context.packageName

        val discoveredEntities = resolveInfos
            .filter { it.activityInfo.packageName != currentPackage }
            .map { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val actName = resolveInfo.activityInfo.name
                val appInfo = resolveInfo.activityInfo.applicationInfo
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val label = resolveInfo.loadLabel(packageManager).toString()
                val category = inferCategory(pkgName, isSystem)

                val pkgInfo = try {
                    packageManager.getPackageInfo(pkgName, 0)
                } catch (_: Exception) {
                    null
                }

                InstalledAppEntity(
                    packageName = pkgName,
                    activityName = actName,
                    defaultLabel = label,
                    category = category.name,
                    installTime = pkgInfo?.firstInstallTime ?: 0L,
                    lastUpdateTime = pkgInfo?.lastUpdateTime ?: 0L
                )
            }

        dao.syncApps(discoveredEntities)
    }

    suspend fun setCustomLabel(packageName: String, label: String) = withContext(Dispatchers.IO) {
        dao.setCustomLabel(packageName, label.trim().takeIf { it.isNotBlank() })
    }

    suspend fun togglePinDock(packageName: String) = withContext(Dispatchers.IO) {
        val existing = dao.getAppByPackageName(packageName) ?: return@withContext
        dao.setPinnedDock(packageName, !existing.isPinnedDock)
    }

    suspend fun togglePinHome(packageName: String) = withContext(Dispatchers.IO) {
        val existing = dao.getAppByPackageName(packageName) ?: return@withContext
        dao.setPinnedHome(packageName, !existing.isPinnedHome)
    }

    suspend fun toggleHideApp(packageName: String) = withContext(Dispatchers.IO) {
        val existing = dao.getAppByPackageName(packageName) ?: return@withContext
        dao.setHidden(packageName, !existing.isHidden)
    }

    suspend fun recordAppLaunch(packageName: String) = withContext(Dispatchers.IO) {
        dao.recordLaunch(packageName)
    }

    fun getAppIcon(packageName: String, activityName: String): Drawable? {
        val key = "$packageName/$activityName"
        val cached = iconCache.get(key)
        if (cached != null) return cached

        val icon = try {
            packageManager.getActivityIcon(ComponentName(packageName, activityName))
        } catch (_: Exception) {
            try {
                packageManager.getApplicationIcon(packageName)
            } catch (_: Exception) {
                null
            }
        }

        if (icon != null) {
            iconCache.put(key, icon)
        }
        return icon
    }

    private fun InstalledAppEntity.toAppItem(): AppItem {
        val categoryEnum = try {
            AppCategory.valueOf(category)
        } catch (_: Exception) {
            AppCategory.ALL
        }

        val icon = getAppIcon(packageName, activityName)

        return AppItem(
            packageName = packageName,
            activityName = activityName,
            defaultLabel = defaultLabel,
            customLabel = customLabel,
            icon = icon,
            isPinnedHome = isPinnedHome,
            isPinnedDock = isPinnedDock,
            isHidden = isHidden,
            category = categoryEnum,
            launchCount = launchCount
        )
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
