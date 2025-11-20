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
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.SleepViewModel
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepScreen(
    onNavigateBack: () -> Unit,
    viewModel: SleepViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(AppConstants.currentDate) }
    val dayStartTimeAsText by viewModel.dayStartTimeAsText.collectAsState()
    val sleepDataList by viewModel.dailySleepData.collectAsState()
    val associatedDataList by viewModel.associatedData.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readSleepData(currentDate)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

    HealthDetailScaffold(
        title = "Sleep",
        dateText = dayStartTimeAsText,
        currentDate = currentDate,
        onNavigateBack = onNavigateBack,
        onDateSelected = { date ->
            currentDate = date
            viewModel.readSleepData(date)
        },
        onPreviousDate = {
            if (currentDate > AppConstants.minimumDate) {
                currentDate = currentDate.minusDays(1)
                viewModel.readSleepData(currentDate)
            }
        },
        onNextDate = {
            if (currentDate < AppConstants.currentDate) {
                currentDate = currentDate.plusDays(1)
                viewModel.readSleepData(currentDate)
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
                items(sleepDataList) { sleepData ->
                    SleepSessionCard(sleepData, associatedDataList)
                }

                if (sleepDataList.isEmpty()) {
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
                                    "No sleep data for this day",
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
fun SleepSessionCard(
    sleepData: HealthDataPoint,
    associatedDataList: List<com.samsung.android.sdk.health.data.data.AssociatedDataPoints>
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = LocalDateTime.ofInstant(sleepData.startTime, sleepData.zoneOffset)
    val endTime = LocalDateTime.ofInstant(sleepData.endTime, sleepData.zoneOffset)
    val duration = Duration.between(sleepData.startTime, sleepData.endTime)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sleep Session",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Start", style = MaterialTheme.typography.bodySmall)
                    Text(startTime.format(timeFormatter), style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text("End", style = MaterialTheme.typography.bodySmall)
                    Text(endTime.format(timeFormatter), style = MaterialTheme.typography.bodyLarge)
                }
                Column {
                    Text("Duration", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${hours}h ${minutes}m",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // TODO: Uncomment when DataType.SleepType.STAGE is available in SDK
            /*
            sleepData.getValue(DataType.SleepType.STAGE)?.let { stage ->
                Spacer(modifier = Modifier.height(8.dp))
                Text("Stage: ${stage.name}", style = MaterialTheme.typography.bodyMedium)
            }
            */

            // Show associated data (blood oxygen, skin temperature)
            // TODO: Uncomment when BloodOxygenType.SPO2 and SkinTemperatureType.CELSIUS are available in SDK
            /*
            val associated = associatedDataList.find { it.uid == sleepData.uid }
            associated?.let { assoc ->
                val skinTemp = assoc.associatedData[DataType.SleepType.Associates.SKIN_TEMPERATURE]
                val bloodOxygen = assoc.associatedData[DataType.SleepType.Associates.BLOOD_OXYGEN]

                if (!skinTemp.isNullOrEmpty() || !bloodOxygen.isNullOrEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    bloodOxygen?.firstOrNull()?.getValue(com.samsung.android.sdk.health.data.request.DataType.BloodOxygenType.SPO2)?.let { spo2 ->
                        Text("Blood Oxygen: $spo2${AppConstants.BLOOD_OXYGEN_UNIT}")
                    }

                    skinTemp?.firstOrNull()?.getValue(com.samsung.android.sdk.health.data.request.DataType.SkinTemperatureType.CELSIUS)?.let { temp ->
                        Text("Skin Temperature: $temp${AppConstants.SKIN_TEMP_UNIT}")
                    }
                }
            }
            */
        }
    }
}
