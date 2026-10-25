package com.ottoscreen.launcher

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.ottoscreen.launcher.ui.home.HomeScreen
import com.ottoscreen.launcher.ui.settings.SettingsScreen
import com.ottoscreen.launcher.ui.theme.AutomotiveColors
import com.ottoscreen.launcher.ui.theme.OttoScreenTheme

/**
 * Ottocast OttoScreen AI - Main Home Launcher Activity
 * Declared with launchMode="singleTask", stateNotNeeded="true", sensorLandscape
 */
class MainActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf<LauncherScreen>(LauncherScreen.Home)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup true automotive full-screen edge-to-edge for 1920x720 display
        setupImmersiveWindow()

        setContent {
            OttoScreenTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AutomotiveColors.Background)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState is LauncherScreen.Settings) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width / 2 } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }
                        },
                        label = "LauncherScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is LauncherScreen.Home -> HomeScreen(
                                onOpenSettings = { currentScreen = LauncherScreen.Settings }
                            )
                            is LauncherScreen.Settings -> SettingsScreen(
                                onNavigateBack = { currentScreen = LauncherScreen.Home }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applyImmersiveFullscreen()
    }

    private fun setupImmersiveWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        applyImmersiveFullscreen()
    }

    private fun applyImmersiveFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentScreen is LauncherScreen.Settings) {
            currentScreen = LauncherScreen.Home
        }
        // Do not exit app when on home screen (it's the home launcher)
    }
}

sealed interface LauncherScreen {
    data object Home : LauncherScreen
    data object Settings : LauncherScreen
}