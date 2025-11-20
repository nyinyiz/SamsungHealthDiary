package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.MonthlyCalendar
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.ViewMode
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.ViewModeToggle
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.WeeklyStepChart
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.StepViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StepScreen(
    onNavigateBack: () -> Unit,
    viewModel: StepViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(ViewMode.DAY) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    
    val totalStepCount by viewModel.totalStepCount.collectAsState()
    val stepDataList by viewModel.totalStepCountData.collectAsState()
    val weeklyStepDataList by viewModel.weeklyStepData.collectAsState()
    val monthlyStepDataList by viewModel.monthlyStepData.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    // Helper function to get Sunday of the week
    fun getSundayOfWeek(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.value % 7 // 0 = Sunday
        return date.minusDays(dayOfWeek.toLong())
    }

    // Map domain StepData to UI format for Weekly Chart
    val weeklyData = remember(weeklyStepDataList, currentDate) {
        val weekStart = getSundayOfWeek(currentDate)
        
        // Initialize map with 0 steps for all days in week
        val weekMap = (0..6).associate { offset ->
            weekStart.plusDays(offset.toLong()) to 0L
        }.toMutableMap()

        // Fill with actual data
        weeklyStepDataList.forEach { stepData ->
            val date = LocalDateTime.ofInstant(stepData.startTime, java.time.ZoneId.systemDefault()).toLocalDate()
            if (weekMap.containsKey(date)) {
                weekMap[date] = stepData.count
            }
        }
        
        // Convert to list of pairs sorted by date
        weekMap.toList().sortedBy { it.first }
    }
    
    val weekStartDate = remember(currentDate) { getSundayOfWeek(currentDate) }
    val weekEndDate = remember(currentDate) { getSundayOfWeek(currentDate).plusDays(6) }

    val monthlyData = remember(monthlyStepDataList, currentDate) {
        val yearMonth = YearMonth.from(currentDate)
        val daysInMonth = yearMonth.lengthOfMonth()
        
        // Initialize map with 0 steps for all days in month
        val monthMap = (1..daysInMonth).associate { day ->
            yearMonth.atDay(day) to 0L
        }.toMutableMap()

        // Fill with actual data
        monthlyStepDataList.forEach { stepData ->
            val date = LocalDateTime.ofInstant(stepData.startTime, java.time.ZoneId.systemDefault()).toLocalDate()
            if (monthMap.containsKey(date)) {
                monthMap[date] = stepData.count
            }
        }
        monthMap
    }

    // Pager for swipe navigation
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

    // Load data when date or page changes
    LaunchedEffect(pagerState.currentPage, viewMode) {
        val offset = pagerState.currentPage - 500
        val newDate = when (viewMode) {
            ViewMode.DAY -> LocalDate.now().plusDays(offset.toLong())
            ViewMode.WEEK -> LocalDate.now().plusWeeks(offset.toLong())
            ViewMode.MONTH -> LocalDate.now().plusMonths(offset.toLong())
        }
        currentDate = newDate
        
        if (viewMode == ViewMode.DAY) {
            viewModel.readStepData(currentDate.atStartOfDay())
        } else if (viewMode == ViewMode.WEEK) {
            val start = getSundayOfWeek(currentDate)
            val end = start.plusDays(6)
            viewModel.readWeeklySteps(start, end)
        } else if (viewMode == ViewMode.MONTH) {
            val start = YearMonth.from(currentDate).atDay(1)
            val end = YearMonth.from(currentDate).atEndOfMonth()
            viewModel.readMonthlySteps(start, end)
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
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LocalGradientColors.current.glassBackground)
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column {
                    Text(
                        text = "Steps",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalGradientColors.current.textPrimary
                    )
                    Text(
                        text = when (viewMode) {
                            ViewMode.DAY -> currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                            ViewMode.WEEK -> "Week of ${currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1).format(DateTimeFormatter.ofPattern("MMM dd"))}"
                            ViewMode.MONTH -> currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalGradientColors.current.textSecondary
                    )
                }
            }

            // View Mode Toggle
            ViewModeToggle(
                selectedMode = viewMode,
                onModeChange = { 
                    viewMode = it
                    scope.launch {
                        pagerState.scrollToPage(500) // Reset to center
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )

            // Horizontal Pager for swipe navigation
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (viewMode) {
                    ViewMode.DAY -> DayView(
                        totalStepCount = totalStepCount,
                        stepDataList = stepDataList
                    )
                    ViewMode.WEEK -> WeekView(
                        weeklyData = weeklyData,
                        weekStartDate = weekStartDate,
                        weekEndDate = weekEndDate,
                        onDayClick = { date ->
                            currentDate = date
                            viewMode = ViewMode.DAY
                            scope.launch {
                                pagerState.scrollToPage(500)
                            }
                        },
                        onJumpToToday = {
                            currentDate = LocalDate.now()
                            scope.launch {
                                pagerState.scrollToPage(500)
                            }
                        }
                    )
                    ViewMode.MONTH -> MonthView(
                        monthlyData = monthlyData,
                        currentMonth = YearMonth.from(currentDate),
                        onDayClick = { date ->
                            currentDate = date
                            viewMode = ViewMode.DAY
                            scope.launch {
                                pagerState.scrollToPage(500)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayView(
    totalStepCount: String,
    stepDataList: List<com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Steps Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GlassWhite20, GlassWhite10)
                        )
                    )
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(40.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ElectricBlue.copy(alpha = 0.3f),
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
                    Text(
                        text = "Total Steps",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalGradientColors.current.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalStepCount,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue
                    )
                }
            }
        }

        // Hourly Breakdown Header
        item {
            Text(
                text = "Hourly Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        // Hourly Steps
        items(stepDataList) { stepData ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite10)
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val time = LocalDateTime.ofInstant(
                        stepData.startTime,
                        java.time.ZoneId.systemDefault()
                    )

                    Text(
                        text = time.format(timeFormatter),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalGradientColors.current.textPrimary
                    )

                    Text(
                        text = "${stepData.count} steps",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanGlow
                    )
                }
            }
        }

        // Empty state
        if (stepDataList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No step data for this day",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextDisabled
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekView(
    weeklyData: List<Pair<LocalDate, Long>>,
    weekStartDate: LocalDate,
    weekEndDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    onJumpToToday: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val maxSteps = weeklyData.maxOfOrNull { it.second } ?: 0L
        
        WeeklyStepChart(
            weeklyData = weeklyData,
            maxSteps = maxSteps,
            weekStartDate = weekStartDate,
            weekEndDate = weekEndDate,
            onDayClick = onDayClick,
            onJumpToToday = onJumpToToday
        )

        Text(
            text = "Tap a bar to view that day's details",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = TextDisabled,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun MonthView(
    monthlyData: Map<LocalDate, Long>,
    currentMonth: YearMonth,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MonthlyCalendar(
            monthlyData = monthlyData,
            selectedMonth = currentMonth,
            onDayClick = onDayClick
        )

        Text(
            text = "Tap a day to view details",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = TextDisabled,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
