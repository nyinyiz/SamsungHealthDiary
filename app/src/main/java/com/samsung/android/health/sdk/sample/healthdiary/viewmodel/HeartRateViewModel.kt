package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.domain.model.HeartRateData
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.GetHeartRateUseCase
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject constructor(
    private val getHeartRateUseCase: GetHeartRateUseCase
) : ViewModel() {

    private val _dailyHeartRate = MutableStateFlow<List<HeartRateUiModel>>(emptyList())
    val dailyHeartRate: StateFlow<List<HeartRateUiModel>> = _dailyHeartRate.asStateFlow()

    private val _dayStartTimeAsText = MutableStateFlow("")
    val dayStartTimeAsText: StateFlow<String> = _dayStartTimeAsText.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val hrResultList: MutableList<HeartRateUiModel> = mutableListOf()

    fun readHeartRateData(dateTime: LocalDateTime) {
        _dayStartTimeAsText.value = dateTime.format(dateFormat)
        val date = dateTime.toLocalDate()

        viewModelScope.launch {
            getHeartRateUseCase(date)
                .onSuccess { heartRateList ->
                    processReadDataResponse(heartRateList)
                }
                .onFailure { error ->
                    _exceptionResponse.value = error
                }
        }
    }

    private fun processReadDataResponse(heartRateList: List<HeartRateData>) {
        hrResultList.clear()
        val hrOfFirstQuarter = HeartRateUiModel(1000f, 0f, 0f, "00:00", "06:00", 0)
        val hrOfSecondQuarter = HeartRateUiModel(1000f, 0f, 0f, "06:00", "12:00", 0)
        val hrOfThirdQuarter = HeartRateUiModel(1000f, 0f, 0f, "12:00", "18:00", 0)
        val hrOfFourthQuarter = HeartRateUiModel(1000f, 0f, 0f, "18:00", "24:00", 0)

        heartRateList.forEach { heartRateData ->
            val time = LocalDateTime.ofInstant(heartRateData.startTime, java.time.ZoneId.systemDefault())
            when {
                time.isBetween(0, 5) -> processHeartRateData(heartRateData, hrOfFirstQuarter)
                time.isBetween(6, 11) -> processHeartRateData(heartRateData, hrOfSecondQuarter)
                time.isBetween(12, 17) -> processHeartRateData(heartRateData, hrOfThirdQuarter)
                time.isBetween(18, 23) -> processHeartRateData(heartRateData, hrOfFourthQuarter)
            }
        }

        processAvgData(hrOfFirstQuarter)
        processAvgData(hrOfSecondQuarter)
        processAvgData(hrOfThirdQuarter)
        processAvgData(hrOfFourthQuarter)

        _dailyHeartRate.value = hrResultList.toList()
    }

    data class HeartRateUiModel(
        var min: Float,
        var max: Float,
        var avg: Float,
        var startTime: String,
        var endTime: String,
        var count: Int
    )

    private fun processHeartRateData(heartRateData: HeartRateData, hrQuarter: HeartRateUiModel) {
        hrQuarter.apply {
            if (heartRateData.heartRate > 0) {
                avg += heartRateData.heartRate
                count++
            }
            if (heartRateData.maxHeartRate > 0) {
                max = maxOf(max, heartRateData.maxHeartRate)
            }
            if (heartRateData.minHeartRate > 0) {
                if (min == 1000f) min = heartRateData.minHeartRate else min = minOf(min, heartRateData.minHeartRate)
            }
        }
    }

    private fun processAvgData(hrQuarter: HeartRateUiModel) {
        hrQuarter.apply {
            if (hrQuarter.count != 0) {
                hrQuarter.avg /= hrQuarter.count
                if (hrQuarter.min == 1000f) hrQuarter.min = 0f 
                hrResultList.add(hrQuarter)
            }
        }
    }

    private fun LocalDateTime.isBetween(fromHour: Int, toHour: Int) =
        this >= this.withHour(fromHour).withMinute(0).withSecond(0) &&
                this <= this.withHour(toHour).withMinute(59).withSecond(59)

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
