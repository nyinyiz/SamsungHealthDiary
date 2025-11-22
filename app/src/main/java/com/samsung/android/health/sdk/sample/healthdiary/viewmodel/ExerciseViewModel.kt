package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
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

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val healthDataStore: HealthDataStore
) : ViewModel() {

    private val TAG = "ExerciseViewModel"

    private val _exerciseList = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val exerciseList: StateFlow<List<HealthDataPoint>> = _exerciseList.asStateFlow()

    private val _weeklyExerciseList = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val weeklyExerciseList: StateFlow<List<HealthDataPoint>> = _weeklyExerciseList.asStateFlow()

    private val _monthlyExerciseList = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val monthlyExerciseList: StateFlow<List<HealthDataPoint>> = _monthlyExerciseList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            Log.e(TAG, "Error in ExerciseViewModel", exception)
            _exceptionResponse.emit(exception)
            _isLoading.value = false
        }
    }

    // Read exercise data for a specific day
    fun readDayExerciseData(date: LocalDate) {
        Log.d(TAG, "Reading exercise data for date: $date")
        _isLoading.value = true
        
        val startDateTime = date.atStartOfDay()
        val endDateTime = date.plusDays(1).atStartOfDay()
        
        val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
        
        val readRequest = DataTypes.EXERCISE.readDataRequestBuilder
            .setLocalTimeFilter(localTimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            try {
                val exerciseDataList = healthDataStore.readData(readRequest).dataList
                Log.d(TAG, "Successfully read ${exerciseDataList.size} exercise records for day")
                _exerciseList.value = exerciseDataList
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read exercise data", e)
                _exceptionResponse.emit(e)
                _isLoading.value = false
            }
        }
    }

    // Read exercise data for a week
    fun readWeeklyExerciseData(startDate: LocalDate, endDate: LocalDate) {
        Log.d(TAG, "Reading weekly exercise data from $startDate to $endDate")
        _isLoading.value = true
        
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.plusDays(1).atStartOfDay()
        
        val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
        
        val readRequest = DataTypes.EXERCISE.readDataRequestBuilder
            .setLocalTimeFilter(localTimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            try {
                val exerciseDataList = healthDataStore.readData(readRequest).dataList
                Log.d(TAG, "Successfully read ${exerciseDataList.size} exercise records for week")
                _weeklyExerciseList.value = exerciseDataList
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read weekly exercise data", e)
                _exceptionResponse.emit(e)
                _isLoading.value = false
            }
        }
    }

    // Read exercise data for a month
    fun readMonthlyExerciseData(startDate: LocalDate, endDate: LocalDate) {
        Log.d(TAG, "Reading monthly exercise data from $startDate to $endDate")
        _isLoading.value = true
        
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.plusDays(1).atStartOfDay()
        
        val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
        
        val readRequest = DataTypes.EXERCISE.readDataRequestBuilder
            .setLocalTimeFilter(localTimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            try {
                val exerciseDataList = healthDataStore.readData(readRequest).dataList
                Log.d(TAG, "Successfully read ${exerciseDataList.size} exercise records for month")
                _monthlyExerciseList.value = exerciseDataList
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read monthly exercise data", e)
                _exceptionResponse.emit(e)
                _isLoading.value = false
            }
        }
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
