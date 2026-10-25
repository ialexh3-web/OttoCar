package com.ottoscreen.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AutomotiveColors.ElectricBlue,
    onPrimary = Color.Black,
    secondary = AutomotiveColors.SafetyOrange,
    onSecondary = Color.Black,
    background = AutomotiveColors.Background,
    surface = AutomotiveColors.CardSurface,
    onBackground = AutomotiveColors.TextPrimary,
    onSurface = AutomotiveColors.TextPrimary
)

@Composable
fun OttoScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}