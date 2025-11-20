package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.GetStepsUseCase
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.GetTotalStepsUseCase
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.GetWeeklyStepsUseCase
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val getStepsUseCase: GetStepsUseCase,
    private val getTotalStepsUseCase: GetTotalStepsUseCase,
    private val getWeeklyStepsUseCase: GetWeeklyStepsUseCase
) : ViewModel() {

    private val _totalStepCountData = MutableStateFlow<List<StepData>>(emptyList())
    val totalStepCountData: StateFlow<List<StepData>> = _totalStepCountData.asStateFlow()

    private val _weeklyStepData = MutableStateFlow<List<StepData>>(emptyList())
    val weeklyStepData: StateFlow<List<StepData>> = _weeklyStepData.asStateFlow()

    private val _totalStepCount = MutableStateFlow("0")
    val totalStepCount: StateFlow<String> = _totalStepCount.asStateFlow()

    private val _dayStartTimeAsText = MutableStateFlow("")
    val dayStartTimeAsText: StateFlow<String> = _dayStartTimeAsText.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    fun readStepData(dateTime: LocalDateTime) {
        Timber.d("StepViewModel: Reading step data for $dateTime")
        _dayStartTimeAsText.value = dateTime.format(dateFormat)
        
        val date = dateTime.toLocalDate()

        viewModelScope.launch {
            // Fetch step data using use case
            getStepsUseCase(date)
                .onSuccess { stepDataList ->
                    _totalStepCountData.value = stepDataList
                    Timber.d("StepViewModel: Loaded ${stepDataList.size} step records")
                }
                .onFailure { error ->
                    Timber.e(error, "StepViewModel: Failed to load step data")
                    _exceptionResponse.value = error
                }

            // Fetch total step count
            getTotalStepsUseCase(date)
                .onSuccess { total ->
                    _totalStepCount.value = total.toString()
                    Timber.d("StepViewModel: Total steps = $total")
                }
                .onFailure { error ->
                    Timber.e(error, "StepViewModel: Failed to load total steps")
                    _exceptionResponse.value = error
                }
        }
    }

    private val _monthlyStepData = MutableStateFlow<List<StepData>>(emptyList())
    val monthlyStepData: StateFlow<List<StepData>> = _monthlyStepData.asStateFlow()

    fun readWeeklySteps(startDate: LocalDate, endDate: LocalDate) {
        Timber.d("StepViewModel: Reading weekly steps from $startDate to $endDate")
        viewModelScope.launch {
            getWeeklyStepsUseCase(startDate, endDate)
                .onSuccess { stepDataList ->
                    _weeklyStepData.value = stepDataList
                    Timber.d("StepViewModel: Loaded ${stepDataList.size} daily records")
                }
                .onFailure { error ->
                    Timber.e(error, "StepViewModel: Failed to load weekly steps")
                    _exceptionResponse.value = error
                }
        }
    }

    fun readMonthlySteps(startDate: LocalDate, endDate: LocalDate) {
        Timber.d("StepViewModel: Reading monthly steps from $startDate to $endDate")
        viewModelScope.launch {
            getWeeklyStepsUseCase(startDate, endDate)
                .onSuccess { stepDataList ->
                    _monthlyStepData.value = stepDataList
                    Timber.d("StepViewModel: Loaded ${stepDataList.size} daily records for month")
                }
                .onFailure { error ->
                    Timber.e(error, "StepViewModel: Failed to load monthly steps")
                    _exceptionResponse.value = error
                }
        }
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
