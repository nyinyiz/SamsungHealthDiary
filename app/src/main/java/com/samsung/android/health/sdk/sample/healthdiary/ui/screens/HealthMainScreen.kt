package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.samsung.android.health.sdk.sample.healthdiary.navigation.Screen
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.GlassCard
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthMainViewModel
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.sdk.health.data.helper.SdkVersion
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMainScreen(
    navController: NavController,
    viewModel: HealthMainViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val permissionResponse by viewModel.permissionResponse.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

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
                    colors = listOf(
                        DeepBlack,
                        CosmicNavy
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Health Diary",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Track your wellness journey",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Health Categories
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(
                    title = "Steps",
                    icon = Icons.Default.Info,
                    iconTint = ElectricBlue,
                    glowColor = ElectricBlue,
                    onClick = {
                        val permSet = mutableSetOf(Permission.of(DataTypes.STEPS, AccessType.READ))
                        viewModel.checkForPermission(context, permSet, AppConstants.STEP_ACTIVITY)
                    }
                )

                GlassCard(
                    title = "Heart Rate",
                    icon = Icons.Default.Favorite,
                    iconTint = HotPink,
                    glowColor = HotPink,
                    onClick = {
                        val permSet = mutableSetOf(Permission.of(DataTypes.HEART_RATE, AccessType.READ))
                        viewModel.checkForPermission(context, permSet, AppConstants.HEART_RATE_ACTIVITY)
                    }
                )

                GlassCard(
                    title = "Sleep",
                    icon = Icons.Default.Info,
                    iconTint = NeonPurple,
                    glowColor = NeonPurple,
                    onClick = {
                        val permSet = mutableSetOf(
                            Permission.of(DataTypes.SLEEP, AccessType.READ),
                            Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
                            Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ)
                        )
                        viewModel.checkForPermission(context, permSet, AppConstants.SLEEP_ACTIVITY)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // SDK Version
            Text(
                text = "SDK Version: ${SdkVersion.getVersionName()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDisabled,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
