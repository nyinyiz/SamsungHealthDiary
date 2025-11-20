package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataTypes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChooseFoodViewModel(private val healthDataStore: HealthDataStore) :
    ViewModel() {

    private val _nutritionInsertResponse = MutableStateFlow(false)
    val nutritionInsertResponse: StateFlow<Boolean> = _nutritionInsertResponse.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun insertNutritionData(nutritionData: HealthDataPoint) {
        val insertRequest = DataTypes.NUTRITION.insertDataRequestBuilder
            .addData(nutritionData)
            .build()

        /**  Make SDK call to insert nutrition data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            healthDataStore.insertData(insertRequest)
            _nutritionInsertResponse.value = true
        }
    }

    fun resetInsertResponse() {
        _nutritionInsertResponse.value = false
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
