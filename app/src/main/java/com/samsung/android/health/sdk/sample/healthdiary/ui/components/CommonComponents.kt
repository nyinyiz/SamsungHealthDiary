package com.samsung.android.health.sdk.sample.healthdiary.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDetailScaffold(
    title: String,
    dateText: String,
    currentDate: LocalDateTime,
    minimumDate: LocalDateTime = AppConstants.minimumDate,
    onNavigateBack: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply {
            set(currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = LocalDateTime.of(year, month + 1, day, 0, 0)
                onDateSelected(selected)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = Calendar.getInstance().timeInMillis
            show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            DateNavigationBar(
                dateText = dateText,
                canGoBack = currentDate > minimumDate,
                canGoForward = currentDate < AppConstants.currentDate,
                onPreviousClick = onPreviousDate,
                onNextClick = onNextDate,
                onCalendarClick = { showDatePicker = true }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount > 50f) {
                                // Swipe right - previous date
                                if (currentDate > minimumDate) onPreviousDate()
                            } else if (dragAmount < -50f) {
                                // Swipe left - next date
                                if (currentDate < AppConstants.currentDate) onNextDate()
                            }
                        }
                    }
            ) {
                content(padding)
            }
        }
    }
}

@Composable
fun DateNavigationBar(
    dateText: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousClick,
                enabled = canGoBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous day",
                    tint = if (canGoBack) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            TextButton(onClick = onCalendarClick) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(dateText, style = MaterialTheme.typography.titleMedium)
            }

            IconButton(
                onClick = onNextClick,
                enabled = canGoForward
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next day",
                    tint = if (canGoForward) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}
