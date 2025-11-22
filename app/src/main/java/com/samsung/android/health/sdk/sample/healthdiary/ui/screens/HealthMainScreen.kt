package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.samsung.android.health.sdk.sample.healthdiary.navigation.Screen
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.GlassCard
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthMainViewModel
import com.samsung.android.sdk.health.data.helper.SdkVersion
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import androidx.hilt.navigation.compose.hiltViewModel

data class HealthCategory(
    val title: String,
    val emoji: String,
    val iconTint: Color,
    val glowColor: Color,
    val activityType: Int,
    val onClick: (android.content.Context, HealthMainViewModel) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMainScreen(
    navController: NavController,
    viewModel: HealthMainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionResponse by viewModel.permissionResponse.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    val healthCategories = listOf(
        HealthCategory(
            title = "Steps",
            emoji = "👟",
            iconTint = ElectricBlue,
            glowColor = ElectricBlue,
            activityType = AppConstants.STEP_ACTIVITY,
            onClick = { ctx, vm ->
                val permSet = mutableSetOf(Permission.of(DataTypes.STEPS, AccessType.READ))
                vm.checkForPermission(ctx, permSet, AppConstants.STEP_ACTIVITY)
            }
        ),
        HealthCategory(
            title = "Heart Rate",
            emoji = "❤️",
            iconTint = HotPink,
            glowColor = HotPink,
            activityType = AppConstants.HEART_RATE_ACTIVITY,
            onClick = { ctx, vm ->
                val permSet = mutableSetOf(Permission.of(DataTypes.HEART_RATE, AccessType.READ))
                vm.checkForPermission(ctx, permSet, AppConstants.HEART_RATE_ACTIVITY)
            }
        ),
        HealthCategory(
            title = "Sleep",
            emoji = "😴",
            iconTint = NeonPurple,
            glowColor = NeonPurple,
            activityType = AppConstants.SLEEP_ACTIVITY,
            onClick = { ctx, vm ->
                val permSet = mutableSetOf(
                    Permission.of(DataTypes.SLEEP, AccessType.READ),
                    Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
                    Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ)
                )
                vm.checkForPermission(ctx, permSet, AppConstants.SLEEP_ACTIVITY)
            }
        ),
        HealthCategory(
            title = "Water Intake",
            emoji = "💧",
            iconTint = CyanGlow,
            glowColor = CyanGlow,
            activityType = AppConstants.WATER_INTAKE_ACTIVITY,
            onClick = { ctx, vm ->
                val permSet = mutableSetOf(Permission.of(DataTypes.WATER_INTAKE, AccessType.READ))
                vm.checkForPermission(ctx, permSet, AppConstants.WATER_INTAKE_ACTIVITY)
            }
        ),
        HealthCategory(
            title = "Workout History",
            emoji = "🏋️",
            iconTint = Color(0xFFFF6B6B),
            glowColor = Color(0xFFFF6B6B),
            activityType = AppConstants.EXERCISE_ACTIVITY,
            onClick = { ctx, vm ->
                val permSet = mutableSetOf(Permission.of(DataTypes.EXERCISE, AccessType.READ))
                vm.checkForPermission(ctx, permSet, AppConstants.EXERCISE_ACTIVITY)
            }
        )
    )

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(permissionResponse) {
        if (permissionResponse.first == AppConstants.SUCCESS) {
            when (permissionResponse.second) {
                AppConstants.STEP_ACTIVITY -> navController.navigate(Screen.Step.route)
                AppConstants.HEART_RATE_ACTIVITY -> navController.navigate(Screen.HeartRate.route)
                AppConstants.SLEEP_ACTIVITY -> navController.navigate(Screen.Sleep.route)
                AppConstants.WATER_INTAKE_ACTIVITY -> navController.navigate(Screen.WaterIntake.route)
                AppConstants.EXERCISE_ACTIVITY -> navController.navigate(Screen.Exercise.route)
            }
            viewModel.resetPermissionResponse()
        } else if (permissionResponse.first != AppConstants.WAITING) {
            showToast(context, permissionResponse.first)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Health Diary",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalGradientColors.current.textPrimary
                    )
                    Text(
                        text = "Track your wellness journey",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalGradientColors.current.textSecondary
                    )
                }

                IconButton(
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LocalGradientColors.current.glassBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = LocalGradientColors.current.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Health Categories Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(healthCategories) { category ->
                    EmojiGlassCard(
                        title = category.title,
                        emoji = category.emoji,
                        iconTint = category.iconTint,
                        glowColor = category.glowColor,
                        onClick = { category.onClick(context, viewModel) }
                    )
                }
            }

            // SDK Version
            Text(
                text = "SDK Version: ${SdkVersion.getVersionName()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LocalGradientColors.current.textDisabled,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun EmojiGlassCard(
    title: String,
    emoji: String,
    iconTint: Color,
    glowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassWhite20,
                            GlassWhite10
                        )
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
                .then(
                    Modifier.background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 300f
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji
                Text(
                    text = emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Title
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalGradientColors.current.textPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
