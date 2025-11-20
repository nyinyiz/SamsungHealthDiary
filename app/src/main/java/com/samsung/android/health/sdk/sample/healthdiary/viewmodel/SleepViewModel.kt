/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AssociatedDataPoints
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.IdFilter
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class SleepViewModel(private val healthDataStore: HealthDataStore) :
    ViewModel() {

    private val _dailySleepData = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val dailySleepData: StateFlow<List<HealthDataPoint>> = _dailySleepData.asStateFlow()

    private val _associatedData = MutableStateFlow<List<AssociatedDataPoints>>(emptyList())
    val associatedData: StateFlow<List<AssociatedDataPoints>> = _associatedData.asStateFlow()

    private val _dayStartTimeAsText = MutableStateFlow("")
    val dayStartTimeAsText: StateFlow<String> = _dayStartTimeAsText.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun readSleepData(dateTime: LocalDateTime) {
        _dayStartTimeAsText.value = dateTime.format(dateFormat)

        val readRequest = DataTypes.SLEEP.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(dateTime, dateTime.plusDays(1)))
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read Sleep data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val sleepDataList = healthDataStore.readData(readRequest).dataList
            _dailySleepData.value = sleepDataList

            if (sleepDataList.isNotEmpty()) {
                val ids = IdFilter.builder()
                sleepDataList.forEach { sleep ->
                    ids.addDataUid(sleep.uid)
                }
                readAssociatedData(ids.build())
            }
        }
    }

    private fun readAssociatedData(idFilter: IdFilter) {
        val associatedReadRequest = DataTypes.SLEEP.associatedReadRequestBuilder
            .setIdFilter(idFilter)
            .addAssociatedDataType(DataType.SleepType.Associates.SKIN_TEMPERATURE)
            .addAssociatedDataType(DataType.SleepType.Associates.BLOOD_OXYGEN)
            .build()

        /**  Make SDK call to read sleep associated data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val associatedList = healthDataStore.readAssociatedData(associatedReadRequest).dataList
            _associatedData.value = associatedList
        }
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
