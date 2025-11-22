package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextDisabled
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextPrimary
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.TextSecondary
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.SleepViewModel
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SleepScreen(
    onNavigateBack: () -> Unit,
    viewModel: SleepViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val sleepDataList by viewModel.dailySleepData.collectAsState()
    val associatedDataList by viewModel.associatedData.collectAsState()
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
        viewModel.readSleepData(currentDate.atStartOfDay())
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
                    text = "Sleep",
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
                    // Total Sleep Summary Card
                    item {
                        val totalDuration = sleepDataList.fold(Duration.ZERO) { acc, sleepData ->
                            acc.plus(Duration.between(sleepData.startTime, sleepData.endTime))
                        }
                        val totalHours = totalDuration.toHours()
                        val totalMinutes = totalDuration.toMinutes() % 60

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
                                        // .blur(40.dp) Removed to fix crash
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF7E57C2).copy(alpha = 0.3f), // Deep Purple for Sleep
                                                    Color.Transparent
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
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF7E57C2),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Total Sleep",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LocalGradientColors.current.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${totalHours}h ${totalMinutes}m",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LocalGradientColors.current.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Sleep Sessions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LocalGradientColors.current.textPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(sleepDataList) { sleepData ->
                        SleepSessionCard(sleepData)
                    }

                    if (sleepDataList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No sleep data for this day",
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
fun SleepSessionCard(
    sleepData: HealthDataPoint
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = LocalDateTime.ofInstant(sleepData.startTime, sleepData.zoneOffset)
    val endTime = LocalDateTime.ofInstant(sleepData.endTime, sleepData.zoneOffset)
    val duration = Duration.between(sleepData.startTime, sleepData.endTime)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60

    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session",
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
                        text = "${hours}h ${minutes}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SleepStatItem(
                    "Start",
                    startTime.format(timeFormatter),
                    LocalGradientColors.current.textPrimary
                )
                SleepStatItem(
                    "End",
                    endTime.format(timeFormatter),
                    LocalGradientColors.current.textPrimary
                )
            }
        }
    }
}

@Composable
private fun SleepStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = LocalGradientColors.current.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// Helper Container for badges
@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
    }
}
