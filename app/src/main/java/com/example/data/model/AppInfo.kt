package com.example.data.model

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val activityName: String,
    val defaultLabel: String,
    val customLabel: String? = null,
    val icon: Drawable? = null,
    val isPinnedHome: Boolean = false,
    val isPinnedDock: Boolean = false,
    val isHidden: Boolean = false,
    val category: AppCategory = AppCategory.ALL,
    val launchCount: Int = 0
) {
    val displayLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: defaultLabel

    val componentKey: String
        get() = "$packageName/$activityName"
}

enum class AppCategory(val title: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    COMMUNICATION("Chat & Call"),
    PRODUCTIVITY("Productivity"),
    TOOLS("Tools & Media"),
    SYSTEM("System")
}

data class AppContextMenuAction(
    val app: AppItem,
    val showDialog: Boolean = false
)
