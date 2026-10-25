package com.ottoscreen.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.ottoscreen.launcher.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Standard Android PackageManager repository for querying installed apps with launch intents.
 * Strictly uses standard Android APIs with no third-party launcher dependencies.
 */
class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val preferences = LauncherPreferences.getInstance(context)

    /**
     * Queries all installed applications with a MAIN / LAUNCHER intent filter.
     * Filters out:
     * 1. Apps without a valid launch intent
     * 2. This launcher app itself (hide self)
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val hiddenPackages = preferences.getHiddenPackages()
        val ownPackage = context.packageName

        val appsList = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName

            // Requirement: Hide self
            if (packageName == ownPackage) continue

            // Requirement: Filter apps with launch intent
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            val isVisible = !hiddenPackages.contains(packageName)

            appsList.add(
                AppInfo(
                    label = label,
                    packageName = packageName,
                    icon = icon,
                    isVisibleOnHome = isVisible,
                    launchIntent = launchIntent
                )
            )
        }

        // Sort alphabetically by app label
        appsList.sortedBy { it.label.lowercase() }
    }

    /**
     * Returns only the applications that should appear on the home grid.
     */
    suspend fun getVisibleHomeApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getInstalledApps().filter { it.isVisibleOnHome }
    }
}