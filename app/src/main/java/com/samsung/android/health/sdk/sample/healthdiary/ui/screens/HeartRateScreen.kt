package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.HealthDetailScaffold
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.formatString
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HeartRateViewModel

@Composable
fun HeartRateScreen(
    onNavigateBack: () -> Unit,
    viewModel: HeartRateViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(AppConstants.currentDate) }
    val dayStartTimeAsText by viewModel.dayStartTimeAsText.collectAsState()
    val heartRateList by viewModel.dailyHeartRate.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readHeartRateData(currentDate)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

    HealthDetailScaffold(
        title = "Heart Rate",
        dateText = dayStartTimeAsText,
        currentDate = currentDate,
        onNavigateBack = onNavigateBack,
        onDateSelected = { date ->
            currentDate = date
            viewModel.readHeartRateData(date)
        },
        onPreviousDate = {
            if (currentDate > AppConstants.minimumDate) {
                currentDate = currentDate.minusDays(1)
                viewModel.readHeartRateData(currentDate)
            }
        },
        onNextDate = {
            if (currentDate < AppConstants.currentDate) {
                currentDate = currentDate.plusDays(1)
                viewModel.readHeartRateData(currentDate)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(heartRateList) { heartRate ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${heartRate.startTime} - ${heartRate.endTime}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                HeartRateMetric("Min", formatString(heartRate.min))
                                HeartRateMetric("Avg", formatString(heartRate.avg))
                                HeartRateMetric("Max", formatString(heartRate.max))
                            }
                        }
                    }
                }

                if (heartRateList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            ) {
                                Text(
                                    "No heart rate data for this day",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun HeartRateMetric(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "bpm",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
