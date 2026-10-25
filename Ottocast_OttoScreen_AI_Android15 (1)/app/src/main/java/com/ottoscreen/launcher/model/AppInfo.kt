package com.ottoscreen.launcher.model

import android.content.Intent
import android.graphics.drawable.Drawable

/**
 * Represents an installed application discovered on the Android system.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isVisibleOnHome: Boolean = true,
    val launchIntent: Intent? = null
)

/**
 * Represents a configurable shortcut slot on the bottom dock bar.
 */
data class DockShortcut(
    val slotIndex: Int,
    val label: String,
    val defaultCategory: String,
    val assignedPackageName: String,
    val iconName: String
)