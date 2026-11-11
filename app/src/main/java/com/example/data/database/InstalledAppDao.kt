package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppDao {

    @Query("SELECT * FROM installed_apps ORDER BY CASE WHEN custom_label IS NOT NULL AND custom_label != '' THEN custom_label ELSE default_label END COLLATE NOCASE ASC")
    fun getAllAppsFlow(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE is_hidden = 0 ORDER BY CASE WHEN custom_label IS NOT NULL AND custom_label != '' THEN custom_label ELSE default_label END COLLATE NOCASE ASC")
    fun getVisibleAppsFlow(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE is_pinned_dock = 1 ORDER BY CASE WHEN custom_label IS NOT NULL AND custom_label != '' THEN custom_label ELSE default_label END COLLATE NOCASE ASC")
    fun getPinnedDockAppsFlow(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE is_pinned_home = 1 ORDER BY CASE WHEN custom_label IS NOT NULL AND custom_label != '' THEN custom_label ELSE default_label END COLLATE NOCASE ASC")
    fun getPinnedHomeAppsFlow(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE is_hidden = 1 ORDER BY default_label COLLATE NOCASE ASC")
    fun getHiddenAppsFlow(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE is_hidden = 0 AND (default_label LIKE '%' || :query || '%' OR custom_label LIKE '%' || :query || '%' OR package_name LIKE '%' || :query || '%') ORDER BY launch_count DESC, default_label COLLATE NOCASE ASC")
    fun searchAppsFlow(query: String): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE package_name = :packageName LIMIT 1")
    suspend fun getAppByPackageName(packageName: String): InstalledAppEntity?

    @Query("SELECT * FROM installed_apps")
    suspend fun getAllAppsSnapshot(): List<InstalledAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApp(app: InstalledAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<InstalledAppEntity>)

    @Update
    suspend fun updateApp(app: InstalledAppEntity)

    @Query("DELETE FROM installed_apps WHERE package_name = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM installed_apps WHERE package_name NOT IN (:packageNames)")
    suspend fun deleteOrphans(packageNames: List<String>)

    @Query("UPDATE installed_apps SET custom_label = :customLabel WHERE package_name = :packageName")
    suspend fun setCustomLabel(packageName: String, customLabel: String?)

    @Query("UPDATE installed_apps SET is_pinned_dock = :isPinned WHERE package_name = :packageName")
    suspend fun setPinnedDock(packageName: String, isPinned: Boolean)

    @Query("UPDATE installed_apps SET is_pinned_home = :isPinned WHERE package_name = :packageName")
    suspend fun setPinnedHome(packageName: String, isPinned: Boolean)

    @Query("UPDATE installed_apps SET is_hidden = :isHidden WHERE package_name = :packageName")
    suspend fun setHidden(packageName: String, isHidden: Boolean)

    @Query("UPDATE installed_apps SET launch_count = launch_count + 1, last_launch_time = :timestamp WHERE package_name = :packageName")
    suspend fun recordLaunch(packageName: String, timestamp: Long = System.currentTimeMillis())

    @Transaction
    suspend fun syncApps(discoveredApps: List<InstalledAppEntity>) {
        val existingMap = getAllAppsSnapshot().associateBy { it.packageName }
        val mergedList = discoveredApps.map { fresh ->
            val existing = existingMap[fresh.packageName]
            if (existing != null) {
                // Preserve user customizations and telemetry-free launch stats
                fresh.copy(
                    customLabel = existing.customLabel,
                    isPinnedHome = existing.isPinnedHome,
                    isPinnedDock = existing.isPinnedDock,
                    isHidden = existing.isHidden,
                    launchCount = existing.launchCount,
                    lastLaunchTime = existing.lastLaunchTime
                )
            } else {
                fresh
            }
        }

        insertAll(mergedList)

        val freshPackages = discoveredApps.map { it.packageName }
        if (freshPackages.isNotEmpty()) {
            deleteOrphans(freshPackages)
        }
    }
}
