package com.ottoscreen.launcher.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ottoscreen.launcher.model.DockShortcut

/**
 * Purely local SharedPreferences manager for Ottocast OttoScreen AI Launcher.
 * Zero network, zero login, zero Firebase dependencies.
 */
class LauncherPreferences(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Set of package names that the user has chosen to hide from the home grid.
     */
    fun getHiddenPackages(): Set<String> {
        return prefs.getStringSet(KEY_HIDDEN_PACKAGES, emptySet()) ?: emptySet()
    }

    /**
     * Toggles whether an application package is visible on the home grid.
     */
    fun setPackageVisibility(packageName: String, isVisible: Boolean) {
        val currentHidden = getHiddenPackages().toMutableSet()
        if (isVisible) {
            currentHidden.remove(packageName)
        } else {
            currentHidden.add(packageName)
        }
        prefs.edit {
            putStringSet(KEY_HIDDEN_PACKAGES, currentHidden)
        }
    }

    /**
     * Checks if a specific package is visible on the home grid.
     */
    fun isPackageVisible(packageName: String): Boolean {
        return !getHiddenPackages().contains(packageName)
    }

    /**
     * Retrieves the 4 configured bottom dock shortcuts.
     */
    fun getDockShortcuts(): List<DockShortcut> {
        val slot0Pkg = prefs.getString(KEY_DOCK_SLOT_0, "ru.yandex.yandexmaps") ?: "ru.yandex.yandexmaps"
        val slot1Pkg = prefs.getString(KEY_DOCK_SLOT_1, "com.spotify.music") ?: "com.spotify.music"
        val slot2Pkg = prefs.getString(KEY_DOCK_SLOT_2, "com.android.car.dialer") ?: "com.android.car.dialer"
        val slot3Pkg = prefs.getString(KEY_DOCK_SLOT_3, "com.android.car.settings") ?: "com.android.car.settings"

        return listOf(
            DockShortcut(0, "Navigation", "navigation", slot0Pkg, "navigation"),
            DockShortcut(1, "Media", "media", slot1Pkg, "music"),
            DockShortcut(2, "Phone", "communication", slot2Pkg, "phone"),
            DockShortcut(3, "Settings", "settings", slot3Pkg, "settings")
        )
    }

    /**
     * Assigns an application package to a dock slot.
     */
    fun setDockShortcut(slotIndex: Int, packageName: String) {
        prefs.edit {
            when (slotIndex) {
                0 -> putString(KEY_DOCK_SLOT_0, packageName)
                1 -> putString(KEY_DOCK_SLOT_1, packageName)
                2 -> putString(KEY_DOCK_SLOT_2, packageName)
                3 -> putString(KEY_DOCK_SLOT_3, packageName)
            }
        }
    }

    /**
     * Set of package names configured for boot auto-start.
     */
    fun getAutoStartPackages(): Set<String> {
        return prefs.getStringSet(KEY_AUTOSTART_PACKAGES, emptySet()) ?: emptySet()
    }

    fun setPackageAutoStart(packageName: String, enabled: Boolean) {
        val current = getAutoStartPackages().toMutableSet()
        if (enabled) current.add(packageName) else current.remove(packageName)
        prefs.edit { putStringSet(KEY_AUTOSTART_PACKAGES, current) }
    }

    companion object {
        private const val PREFS_NAME = "ottoscreen_launcher_prefs"
        private const val KEY_HIDDEN_PACKAGES = "key_hidden_packages"
        private const val KEY_DOCK_SLOT_0 = "key_dock_slot_0"
        private const val KEY_DOCK_SLOT_1 = "key_dock_slot_1"
        private const val KEY_DOCK_SLOT_2 = "key_dock_slot_2"
        private const val KEY_DOCK_SLOT_3 = "key_dock_slot_3"
        private const val KEY_AUTOSTART_PACKAGES = "key_autostart_packages"

        @Volatile
        private var instance: LauncherPreferences? = null

        fun getInstance(context: Context): LauncherPreferences {
            return instance ?: synchronized(this) {
                instance ?: LauncherPreferences(context).also { instance = it }
            }
        }
    }
}