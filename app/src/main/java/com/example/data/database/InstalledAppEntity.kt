package com.example.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_apps")
data class InstalledAppEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "activity_name")
    val activityName: String,

    @ColumnInfo(name = "default_label")
    val defaultLabel: String,

    @ColumnInfo(name = "custom_label")
    val customLabel: String? = null,

    @ColumnInfo(name = "is_pinned_home")
    val isPinnedHome: Boolean = false,

    @ColumnInfo(name = "is_pinned_dock")
    val isPinnedDock: Boolean = false,

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,

    @ColumnInfo(name = "category")
    val category: String = "ALL",

    @ColumnInfo(name = "install_time")
    val installTime: Long = 0L,

    @ColumnInfo(name = "last_update_time")
    val lastUpdateTime: Long = 0L,

    @ColumnInfo(name = "launch_count")
    val launchCount: Int = 0,

    @ColumnInfo(name = "last_launch_time")
    val lastLaunchTime: Long = 0L
) {
    val displayLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: defaultLabel

    val componentKey: String
        get() = "$packageName/$activityName"
}
