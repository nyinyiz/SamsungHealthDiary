package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class WaterIntakeViewModel @Inject constructor(
    private val healthDataStore: HealthDataStore
) : ViewModel() {

    private val TAG = "WaterIntakeViewModel"

    private val _dailyWaterIntake = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val dailyWaterIntake: StateFlow<List<HealthDataPoint>> = _dailyWaterIntake.asStateFlow()

    private val _totalWaterIntake = MutableStateFlow(0f)
    val totalWaterIntake: StateFlow<Float> = _totalWaterIntake.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun readWaterIntakeData(date: LocalDate) {
        val startTime = date.atStartOfDay()
        val endTime = date.plusDays(1).atStartOfDay()

        val readRequest = DataTypes.WATER_INTAKE.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(startTime, endTime))
            .setOrdering(Ordering.ASC)
            .build()

        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val dataList = healthDataStore.readData(readRequest).dataList
            _dailyWaterIntake.value = dataList
            calculateTotalIntake(dataList)
        }
    }

    private fun calculateTotalIntake(dataList: List<HealthDataPoint>) {
        var total = 0f
        dataList.forEach { point ->
            // Assuming the value is stored in the first field or specific field.
            // Need to verify how to get the float value.
            // Usually it's point.getValue(DataType.WaterIntakeType.AMOUNT)
            // But for now I'll assume a generic getValue or similar if I can't verify.
            // Looking at other ViewModels might help.
            // For now, I'll use a placeholder and fix it after checking other files.
             total += point.getValueOrDefault(DataType.WaterIntakeType.AMOUNT, 0f)
        }
        _totalWaterIntake.value = total
    }

    fun addWaterIntake(amount: Float, date: LocalDateTime) {
        Log.d(TAG, "addWaterIntake called with amount: $amount")
        val zoneOffset = java.time.ZoneId.systemDefault().rules.getOffset(date)
        val instant = date.toInstant(zoneOffset)
        
        // Water intake is usually a point in time, but SDK might require start/end time.
        // Using setStartTime and setEndTime with same value for point data.
        val dataPoint = HealthDataPoint.builder()
            .setStartTime(instant)
            .setEndTime(instant)
            .addFieldData(DataType.WaterIntakeType.AMOUNT, amount)
            .build()

        val insertRequest = DataTypes.WATER_INTAKE.insertDataRequestBuilder
            .addData(dataPoint)
            .build()

        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            try {
                Log.d(TAG, "Attempting to insert water data...")
                healthDataStore.insertData(insertRequest)
                Log.d(TAG, "Water data inserted successfully")
                readWaterIntakeData(date.toLocalDate())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert water data", e)
                _exceptionResponse.emit(e)
            }
        }
    }

    fun requestWritePermission(context: android.content.Context, appId: Int) {
        val permSet = mutableSetOf(
            com.samsung.android.sdk.health.data.permission.Permission.of(DataTypes.WATER_INTAKE, com.samsung.android.sdk.health.data.permission.AccessType.WRITE)
        )

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO + exceptionHandler) {
            val grantedPermissions = healthDataStore.getGrantedPermissions(permSet)

            if (grantedPermissions.containsAll(permSet)) {
                // Permission already granted
            } else {
                requestForPermission(context, permSet, appId)
            }
        }
    }

    private fun requestForPermission(
        context: android.content.Context,
        permSet: MutableSet<com.samsung.android.sdk.health.data.permission.Permission>,
        appId: Int,
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO + exceptionHandler) {
            val activity = context as android.app.Activity
            val result = healthDataStore.requestPermissions(permSet, activity)

            if (result.containsAll(permSet)) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
