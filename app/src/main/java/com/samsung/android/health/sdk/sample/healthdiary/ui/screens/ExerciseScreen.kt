package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.ViewMode
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.ViewModeToggle
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.ExerciseViewModel
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(ViewMode.DAY) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    val exerciseList by viewModel.exerciseList.collectAsState()
    val weeklyExerciseList by viewModel.weeklyExerciseList.collectAsState()
    val monthlyExerciseList by viewModel.monthlyExerciseList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    // Helper function to get Sunday of the week
    fun getSundayOfWeek(date: LocalDate): LocalDate {
        val dayOfWeek = date.dayOfWeek.value % 7 // 0 = Sunday
        return date.minusDays(dayOfWeek.toLong())
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

        when (viewMode) {
            ViewMode.DAY -> {
                viewModel.readDayExerciseData(currentDate)
            }
            ViewMode.WEEK -> {
                val start = getSundayOfWeek(currentDate)
                val end = start.plusDays(6)
                viewModel.readWeeklyExerciseData(start, end)
            }
            ViewMode.MONTH -> {
                val start = YearMonth.from(currentDate).atDay(1)
                val end = YearMonth.from(currentDate).atEndOfMonth()
                viewModel.readMonthlyExerciseData(start, end)
            }
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
                        text = "Workout History",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalGradientColors.current.textPrimary
                    )
                    Text(
                        text = when (viewMode) {
                            ViewMode.DAY -> currentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                            ViewMode.WEEK -> "Week of ${getSundayOfWeek(currentDate).format(DateTimeFormatter.ofPattern("MMM dd"))}"
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
                        exerciseList = exerciseList,
                        isLoading = isLoading
                    )
                    ViewMode.WEEK -> WeekView(
                        exerciseList = weeklyExerciseList,
                        isLoading = isLoading,
                        weekStart = getSundayOfWeek(currentDate)
                    )
                    ViewMode.MONTH -> MonthView(
                        exerciseList = monthlyExerciseList,
                        isLoading = isLoading,
                        currentMonth = YearMonth.from(currentDate)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayView(
    exerciseList: List<HealthDataPoint>,
    isLoading: Boolean
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanGlow)
            }
        }
        exerciseList.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No workouts for this day",
                    fontSize = 18.sp,
                    color = LocalGradientColors.current.textSecondary
                )
            }
        }
        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exerciseList) { exercise ->
                    WorkoutCard(exercise)
                }
            }
        }
    }
}

@Composable
private fun WeekView(
    exerciseList: List<HealthDataPoint>,
    isLoading: Boolean,
    weekStart: LocalDate
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanGlow)
            }
        }
        else -> {
            // Group exercises by day
            val exercisesByDay = remember(exerciseList, weekStart) {
                val grouped = mutableMapOf<LocalDate, MutableList<HealthDataPoint>>()
                exerciseList.forEach { exercise ->
                    val date = exercise.startTime.atZone(ZoneId.systemDefault()).toLocalDate()
                    grouped.getOrPut(date) { mutableListOf() }.add(exercise)
                }
                grouped
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show each day of the week
                (0..6).forEach { offset ->
                    val date = weekStart.plusDays(offset.toLong())
                    val workoutsForDay = exercisesByDay[date] ?: emptyList()
                    
                    item {
                        DaySection(
                            date = date,
                            workouts = workoutsForDay
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthView(
    exerciseList: List<HealthDataPoint>,
    isLoading: Boolean,
    currentMonth: YearMonth
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanGlow)
            }
        }
        exerciseList.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No workouts this month",
                    fontSize = 18.sp,
                    color = LocalGradientColors.current.textSecondary
                )
            }
        }
        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exerciseList) { exercise ->
                    WorkoutCard(exercise)
                }
            }
        }
    }
}

@Composable
private fun DaySection(
    date: LocalDate,
    workouts: List<HealthDataPoint>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEE, MMM dd")),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = LocalGradientColors.current.textPrimary
        )
        
        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "No workouts",
                    fontSize = 14.sp,
                    color = LocalGradientColors.current.textSecondary
                )
            }
        } else {
            workouts.forEach { workout ->
                WorkoutCard(workout)
            }
        }
    }
}

@Composable
private fun WorkoutCard(exercise: HealthDataPoint) {
    val startTime = exercise.startTime
    val endTime = exercise.endTime
    val duration = java.time.Duration.between(startTime, endTime)
    val durationMinutes = duration.toMinutes()
    
    // Extract exercise type - try to get the exercise type field
    val exerciseTypeName = try {
        // The exercise type might be stored as metadata or in a specific field
        // For now, we'll use a placeholder and show "Exercise" with time
        getExerciseTypeName(exercise)
    } catch (e: Exception) {
        "Exercise"
    }
    
    val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = startTime.atZone(ZoneId.systemDefault()).format(dateFormatter)

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = getExerciseColorByName(exerciseTypeName).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getExerciseEmoji(exerciseTypeName),
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Exercise Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exerciseTypeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalGradientColors.current.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 14.sp,
                        color = LocalGradientColors.current.textSecondary
                    )
                    if (durationMinutes > 0) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            color = LocalGradientColors.current.textSecondary
                        )
                        Text(
                            text = "${durationMinutes}min",
                            fontSize = 14.sp,
                            color = LocalGradientColors.current.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// Helper function to extract exercise type name from HealthDataPoint
private fun getExerciseTypeName(exercise: HealthDataPoint): String {
    // For now, return a default "Exercise" since we need to investigate 
    // the exact field name for exercise type in Samsung Health SDK
    // The exercise type might need to be accessed differently
    return "Exercise"
}

// Map exercise type ID to readable name
private fun mapExerciseTypeIdToName(typeId: Int): String {
    return when (typeId) {
        // Cardio
        1001 -> "Walking"
        1002 -> "Running"
        1003 -> "Hiking"
        1004 -> "Treadmill"
        1005 -> "Indoor Walking"
        1006 -> "Stair Climbing"
        
        // Cycling
        11007 -> "Cycling"
        11008 -> "Indoor Cycling"
        11009 -> "Mountain Biking"
        
        // Swimming
        14001 -> "Swimming"
        14002 -> "Pool Swimming"
        14003 -> "Open Water Swimming"
        
        // Sports
        2001 -> "Baseball"
        2002 -> "Football"
        2003 -> "Basketball"
        2004 -> "Volleyball"
        2005 -> "Soccer"
        2006 -> "Tennis"
        2007 -> "Golf"
        2008 -> "Badminton"
        2009 -> "Table Tennis"
        
        // Gym & Fitness
        3001 -> "Weight Training"
        3002 -> "Yoga"
        3003 -> "Pilates"
        3004 -> "CrossFit"
        3005 -> "Aerobics"
        3006 -> "Elliptical"
        3007 -> "Rowing Machine"
        
        // Other
        4001 -> "Dancing"
        4002 -> "Boxing"
        4003 -> "Martial Arts"
        
        0 -> "Custom Workout"
        else -> "Exercise"
    }
}

// Get emoji based on exercise type name
private fun getExerciseEmoji(exerciseName: String): String {
    return when (exerciseName) {
        "Running", "Treadmill" -> "🏃"
        "Walking", "Hiking", "Indoor Walking" -> "🚶"
        "Cycling", "Indoor Cycling", "Mountain Biking" -> "🚴"
        "Swimming", "Pool Swimming", "Open Water Swimming" -> "🏊"
        "Weight Training", "CrossFit" -> "🏋️"
        "Yoga", "Pilates" -> "🧘"
        "Basketball" -> "🏀"
        "Football", "Soccer" -> "⚽"
        "Baseball" -> "⚾"
        "Tennis", "Badminton", "Table Tennis" -> "🎾"
        "Golf" -> "⛳"
        "Boxing", "Martial Arts" -> "🥊"
        "Dancing" -> "💃"
        "Volleyball" -> "🏐"
        else -> "🏃"
    }
}

// Get color based on exercise type
private fun getExerciseColorByName(exerciseName: String): Color {
    return when (exerciseName) {
        "Running", "Treadmill" -> Color(0xFFFF6B6B)
        "Walking", "Hiking" -> Color(0xFF4ECDC4)
        "Cycling", "Indoor Cycling", "Mountain Biking" -> Color(0xFF95E1D3)
        "Swimming", "Pool Swimming", "Open Water Swimming" -> Color(0xFF3498DB)
        "Weight Training", "CrossFit" -> Color(0xFF9B59B6)
        "Yoga", "Pilates" -> Color(0xFFE8A87C)
        "Basketball", "Football", "Soccer", "Baseball" -> Color(0xFFFFA07A)
        else -> Color(0xFFFF6B6B)
    }
}
