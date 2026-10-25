package com.ottoscreen.launcher.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscreen.launcher.data.AppRepository
import com.ottoscreen.launcher.data.LauncherPreferences
import com.ottoscreen.launcher.model.AppInfo
import com.ottoscreen.launcher.model.DockShortcut
import com.ottoscreen.launcher.ui.dock.BottomDock
import com.ottoscreen.launcher.ui.theme.AutomotiveColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Landscape-only Home Screen optimized for Ottocast OttoScreen 11.4" (1920x720).
 * Displays:
 * 1. Top status header with digital clock, weather glance, and app search
 * 2. Center responsive LazyVerticalGrid with installed applications
 * 3. Bottom persistent dock with 4 configurable shortcut slots
 */
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appRepository = remember { AppRepository(context) }
    val preferences = remember { LauncherPreferences.getInstance(context) }

    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var dockShortcuts by remember { mutableStateOf<List<DockShortcut>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    // Time ticker
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now).uppercase()
            delay(1000L)
        }
    }

    // Load apps and dock shortcuts
    LaunchedEffect(Unit) {
        isLoading = true
        installedApps = appRepository.getVisibleHomeApps()
        dockShortcuts = preferences.getDockShortcuts()
        isLoading = false
    }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            val q = searchQuery.lowercase()
            installedApps.filter {
                it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
        }
    }

    val launchApp: (AppInfo) -> Unit = { app ->
        try {
            val intent = app.launchIntent ?: context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Cannot launch ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Launch failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AutomotiveColors.Background)
    ) {
        // ==============================================================
        // 1. TOP AUTOMOTIVE STATUS & SEARCH HEADER                      
        // ==============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(AutomotiveColors.CardSurface)
                .border(1.dp, AutomotiveColors.BorderHairline)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Clock & Weather Glance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = currentTimeString.ifEmpty { "14:30" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Text(
                        text = currentDateString.ifEmpty { "SAT, 25 OCT" },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AutomotiveColors.TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 28.dp)
                        .background(AutomotiveColors.BorderHairline)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF222234))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = AutomotiveColors.SafetyOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+22°C",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // App Search Input
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search apps...", fontSize = 12.sp, color = AutomotiveColors.TextSecondary)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AutomotiveColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF222234),
                    unfocusedContainerColor = Color(0xFF222234),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 32.dp)
                    .height(44.dp)
            )

            // Settings Gear (Min 64dp Touch Target)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onOpenSettings() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF28283C))
                        .border(1.dp, AutomotiveColors.BorderHairline, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Launcher Settings",
                        tint = AutomotiveColors.ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ==============================================================
        // 2. CENTER RESPONSIVE APPS GRID                                
        // ==============================================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AutomotiveColors.ElectricBlue
                )
            } else if (filteredApps.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No apps found on home grid",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Use Launcher Settings to toggle apps back to the home grid.",
                        fontSize = 12.sp,
                        color = AutomotiveColors.TextSecondary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 135.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        AppGridItem(
                            app = app,
                            onLaunch = { launchApp(app) }
                        )
                    }
                }
            }
        }

        // ==============================================================
        // 3. BOTTOM PERSISTENT DOCK (4 Configurable Shortcut Slots)     
        // ==============================================================
        BottomDock(
            dockShortcuts = dockShortcuts,
            onLaunchPackage = { pkgName ->
                val app = installedApps.find { it.packageName == pkgName }
                if (app != null) {
                    launchApp(app)
                } else {
                    val intent = context.packageManager.getLaunchIntentForPackage(pkgName)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        onOpenSettings()
                    }
                }
            },
            onOpenSettings = onOpenSettings
        )
    }
}

/**
 * Individual app tile on the home grid with high-contrast automotive styling
 * and minimum 64dp touch accessibility.
 */
@Composable
fun AppGridItem(
    app: AppInfo,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(AutomotiveColors.CardSurface)
            .border(1.dp, AutomotiveColors.BorderHairline, RoundedCornerShape(18.dp))
            .clickable { onLaunch() }
            .padding(12.dp)
            .height(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon box
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF28283C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.label.take(2).uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AutomotiveColors.ElectricBlue
                )
            }

            Text(
                text = app.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}