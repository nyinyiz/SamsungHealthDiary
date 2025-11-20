/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import com.samsung.android.sdk.health.data.request.Ordering
import com.samsung.android.sdk.health.data.response.DataResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class StepViewModel(private val healthDataStore: HealthDataStore) :
    ViewModel() {

    private val _totalStepCountData = MutableStateFlow<List<AggregatedData<Long>>>(emptyList())
    val totalStepCountData: StateFlow<List<AggregatedData<Long>>> = _totalStepCountData.asStateFlow()

    private val _totalStepCount = MutableStateFlow("0")
    val totalStepCount: StateFlow<String> = _totalStepCount.asStateFlow()

    private val _dayStartTimeAsText = MutableStateFlow("")
    val dayStartTimeAsText: StateFlow<String> = _dayStartTimeAsText.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun readStepData(dateTime: LocalDateTime) {
        _dayStartTimeAsText.value = dateTime.format(dateFormat)

        val localtimeFilter = LocalTimeFilter.of(dateTime, dateTime.plusDays(1))
        val localTimeGroup = LocalTimeGroup.of(LocalTimeGroupUnit.HOURLY, 1)
        val aggregateRequest = DataType.StepsType.TOTAL.requestBuilder
            .setLocalTimeFilterWithGroup(localtimeFilter, localTimeGroup)
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read step data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val result = healthDataStore.aggregateData(aggregateRequest)
            processAggregateDataResponse(result)
        }
    }

    private fun processAggregateDataResponse(
        result: DataResponse<AggregatedData<Long>>
    ) {
        val stepCount = ArrayList<AggregatedData<Long>>()
        var totalSteps: Long = 0

        result.dataList.forEach { stepData ->
            val hourlySteps = stepData.value as Long
            totalSteps += hourlySteps
            stepCount.add(stepData)
        }
        _totalStepCount.value = totalSteps.toString()
        _totalStepCountData.value = stepCount
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
