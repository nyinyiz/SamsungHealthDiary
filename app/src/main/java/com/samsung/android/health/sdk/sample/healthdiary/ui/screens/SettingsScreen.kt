package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.GlassBox
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: com.samsung.android.health.sdk.sample.healthdiary.viewmodel.SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = LocalGradientColors.current.backgroundGradient
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LocalGradientColors.current.glassBackground)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = LocalGradientColors.current.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalGradientColors.current.textPrimary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Section
                SettingsSection(title = "Appearance") {
                    SettingsItem(
                        icon = Icons.Default.Info, // Using Info as safe alternative for DarkMode/Nightlight
                        title = "Dark Mode",
                        subtitle = "Always on",
                        trailing = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.setDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ElectricBlue,
                                    uncheckedThumbColor = LocalGradientColors.current.textSecondary,
                                    uncheckedTrackColor = LocalGradientColors.current.glassBackground
                                )
                            )
                        }
                    )
                }

                // About Section
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0",
                        onClick = {}
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsItem(
                        icon = Icons.Default.Info, // Using Info as safe alternative for Code
                        title = "Project Source",
                        subtitle = "View on GitHub",
                        onClick = {
                            uriHandler.openUri("https://github.com/nyinyiz/SamsungHealthDiary.git")
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = LocalGradientColors.current.textSecondary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LocalGradientColors.current.glassBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocalGradientColors.current.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = LocalGradientColors.current.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = LocalGradientColors.current.textSecondary
                )
            }
        }

        if (trailing != null) {
            trailing()
        }
    }
}
