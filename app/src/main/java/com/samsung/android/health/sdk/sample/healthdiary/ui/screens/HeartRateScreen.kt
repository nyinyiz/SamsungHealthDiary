package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.LocalGradientColors
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextDisabled
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextPrimary
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextSecondary
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.CyanGlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.GlassBox
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HeartRateViewModel
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeartRateScreen(
    onNavigateBack: () -> Unit,
    viewModel: HeartRateViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val heartRateList: List<HeartRateViewModel.HeartRateUiModel> by viewModel.dailyHeartRate.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    // Pager state for swipe navigation
    val pagerState = rememberPagerState(
        initialPage = 500,
        pageCount = { 1000 }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    // Sync pager with date and fetch data
    LaunchedEffect(pagerState.currentPage) {
        val offset = pagerState.currentPage - 500
        currentDate = LocalDate.now().plusDays(offset.toLong())
        viewModel.readHeartRateData(currentDate.atStartOfDay())
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
                    text = "Heart Rate",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalGradientColors.current.textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))

                // Today Button
                if (!currentDate.isEqual(LocalDate.now())) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(500)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = CyanGlow)
                    ) {
                        Text(
                            text = "Today",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Date Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = TextSecondary
                    )
                }

                Text(
                    text = currentDate.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalGradientColors.current.textPrimary
                )

                IconButton(
                    onClick = {
                        if (currentDate.isBefore(LocalDate.now())) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    enabled = currentDate.isBefore(LocalDate.now())
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = if (currentDate.isBefore(LocalDate.now())) TextSecondary else TextDisabled
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card (Latest or Average)
                    item {
                        val latestAvg = heartRateList.lastOrNull()?.avg ?: 0f
                        GlassBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Background Glow
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(120.dp)
                                        .blur(40.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    androidx.compose.ui.graphics.Color(0xFFFF4081)
                                                        .copy(alpha = 0.3f),
                                                    androidx.compose.ui.graphics.Color.Transparent
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color(0xFFFF4081),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Latest Average",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LocalGradientColors.current.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = latestAvg.toInt().toString(),
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LocalGradientColors.current.textPrimary
                                        )
                                        Text(
                                            text = " bpm",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = LocalGradientColors.current.textSecondary,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Daily Breakdown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LocalGradientColors.current.textPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(heartRateList) { heartRate ->
                        GlassBox(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${heartRate.startTime} - ${heartRate.endTime}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LocalGradientColors.current.textSecondary
                                    )

                                    Container(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(LocalGradientColors.current.glassBackground)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${heartRate.count} readings",
                                            fontSize = 12.sp,
                                            color = LocalGradientColors.current.textDisabled
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    HeartRateStatItem(
                                        "Min",
                                        heartRate.min.toInt().toString(),
                                        androidx.compose.ui.graphics.Color(0xFF64B5F6)
                                    )
                                    HeartRateStatItem(
                                        "Avg",
                                        heartRate.avg.toInt().toString(),
                                        androidx.compose.ui.graphics.Color(0xFF81C784)
                                    )
                                    HeartRateStatItem(
                                        "Max",
                                        heartRate.max.toInt().toString(),
                                        androidx.compose.ui.graphics.Color(0xFFE57373)
                                    )
                                }
                            }
                        }
                    }

                    if (heartRateList.size == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No heart rate data for this day",
                                    fontSize = 16.sp,
                                    color = LocalGradientColors.current.textDisabled
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeartRateStatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = LocalGradientColors.current.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "bpm",
            fontSize = 10.sp,
            color = LocalGradientColors.current.textDisabled
        )
    }
}

// Helper Container for "readings" badge
@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
    }
}
