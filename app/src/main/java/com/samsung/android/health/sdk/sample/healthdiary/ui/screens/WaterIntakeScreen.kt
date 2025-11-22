package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.GlassBox
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.CyanGlow
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.LocalGradientColors
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextPrimary
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextSecondary
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.WaterIntakeViewModel
import java.time.LocalDate

@Composable
fun WaterIntakeScreen(
    onNavigateBack: () -> Unit,
    viewModel: WaterIntakeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val totalWaterIntake by viewModel.totalWaterIntake.collectAsState()
    val dailyGoal = 2000f // Default goal 2000ml

    // Animation for water level
    val fillRatio = (totalWaterIntake / dailyGoal).coerceIn(0f, 1f)
    val animatedFillRatio by animateFloatAsState(
        targetValue = fillRatio,
        animationSpec = tween(durationMillis = 1000),
        label = "WaterFillAnimation"
    )

    LaunchedEffect(Unit) {
        viewModel.readWaterIntakeData(LocalDate.now())
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
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
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
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Water Intake",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalGradientColors.current.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Water Bottle / Glass Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassBox(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp,
                        topStart = 10.dp,
                        topEnd = 10.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Water Fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(animatedFillRatio)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF4FC3F7), // Light Blue
                                            Color(0xFF0288D1)  // Darker Blue
                                        )
                                    )
                                )
                        )

                        // Text Overlay
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${(totalWaterIntake).toInt()} ml",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (animatedFillRatio > 0.5f) Color.White else TextPrimary
                            )
                            Text(
                                text = "/ ${dailyGoal.toInt()} ml",
                                fontSize = 14.sp,
                                color = if (animatedFillRatio > 0.5f) Color.White.copy(alpha = 0.7f) else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info text
            Text(
                text = "Today's water intake from Samsung Health",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = LocalGradientColors.current.textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
