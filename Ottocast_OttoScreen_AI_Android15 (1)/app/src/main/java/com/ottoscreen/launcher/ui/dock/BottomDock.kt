package com.ottoscreen.launcher.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottoscreen.launcher.model.DockShortcut
import com.ottoscreen.launcher.ui.theme.AutomotiveColors

/**
 * Bottom Dock Bar with 3-4 configurable shortcut slots.
 * Every touch target conforms to >= 64dp for safe in-car usage.
 */
@Composable
fun BottomDock(
    dockShortcuts: List<DockShortcut>,
    onLaunchPackage: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color(0xFF14141E))
            .border(1.dp, AutomotiveColors.BorderHairline)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AutomotiveColors.ElectricBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Column {
                Text(
                    text = "OttoScreen AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Android 15 Native Launcher",
                    fontSize = 9.sp,
                    color = AutomotiveColors.TextSecondary
                )
            }
        }

        // Center: 4 Configurable Shortcut Slots
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF1B1B28))
                .border(1.dp, AutomotiveColors.BorderHairline, RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // All Apps indicator (Home)
            DockItem(
                icon = Icons.Default.Apps,
                label = "Apps",
                iconTint = AutomotiveColors.ElectricBlue,
                isSelected = true,
                onClick = {}
            )

            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 36.dp)
                    .background(AutomotiveColors.BorderHairline)
            )

            // 4 Configurable Slots
            dockShortcuts.take(4).forEach { slot ->
                val icon = when (slot.defaultCategory) {
                    "navigation" -> Icons.Default.Navigation
                    "media" -> Icons.Default.MusicNote
                    "communication" -> Icons.Default.Phone
                    else -> Icons.Default.Settings
                }
                val tint = when (slot.defaultCategory) {
                    "navigation" -> AutomotiveColors.ElectricBlue
                    "media" -> Color(0xFF1DB954)
                    "communication" -> Color(0xFF10B981)
                    else -> AutomotiveColors.SafetyOrange
                }

                DockItem(
                    icon = icon,
                    label = slot.label,
                    iconTint = tint,
                    isSelected = false,
                    onClick = { onLaunchPackage(slot.assignedPackageName) }
                )
            }

            // Quick Dock Settings
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onOpenSettings() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Configure Dock",
                    tint = AutomotiveColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right Placeholder / Status
        Text(
            text = "1920x720 • 11.4"",
            fontSize = 11.sp,
            color = AutomotiveColors.TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DockItem(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF28283C) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else AutomotiveColors.TextSecondary,
                maxLines = 1
            )
        }
    }
}