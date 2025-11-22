package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
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

    // Wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveOffset"
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
                        // Water Fill with Wave Effect
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val waterHeight = canvasHeight * animatedFillRatio
                            
                            if (waterHeight > 0f) {
                                val wavePath = Path()
                                val waveAmplitude = 8f
                                val waveLength = canvasWidth / 2f
                                
                                // Start from bottom left
                                wavePath.moveTo(0f, canvasHeight)
                                
                                // Draw to bottom of wave
                                wavePath.lineTo(0f, canvasHeight - waterHeight + waveAmplitude)
                                
                                // Draw the wave at the top of water
                                var x = 0f
                                while (x <= canvasWidth) {
                                    val angle = (x / waveLength * Math.PI * 2 + Math.toRadians(waveOffset.toDouble())).toFloat()
                                    val y = canvasHeight - waterHeight + (kotlin.math.sin(angle) * waveAmplitude)
                                    wavePath.lineTo(x, y)
                                    x += 5f
                                }
                                
                                // Complete the path
                                wavePath.lineTo(canvasWidth, canvasHeight)
                                wavePath.close()
                                
                                // Draw the water with gradient
                                drawPath(
                                    path = wavePath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF4FC3F7), // Light Blue
                                            Color(0xFF0288D1)  // Darker Blue
                                        ),
                                        startY = canvasHeight - waterHeight,
                                        endY = canvasHeight
                                    )
                                )
                            }
                        }

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
