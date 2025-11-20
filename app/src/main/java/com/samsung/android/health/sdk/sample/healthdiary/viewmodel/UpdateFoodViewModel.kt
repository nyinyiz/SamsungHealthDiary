package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.IdFilter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdateFoodViewModel(private val healthDataStore: HealthDataStore) :
    ViewModel() {

    private val _nutritionUpdateResponse = MutableStateFlow(false)
    val nutritionUpdateResponse: StateFlow<Boolean> = _nutritionUpdateResponse.asStateFlow()

    private val _nutritionDeleteResponse = MutableStateFlow(false)
    val nutritionDeleteResponse: StateFlow<Boolean> = _nutritionDeleteResponse.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun updateNutritionData(uid: String, nutritionData: HealthDataPoint) {
        val updateRequest = DataTypes.NUTRITION.updateDataRequestBuilder
            .addDataWithUid(uid, nutritionData)
            .build()

        /**  Make SDK call to update nutrition data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            healthDataStore.updateData(updateRequest)
            _nutritionUpdateResponse.value = true
        }
    }

    fun deleteNutritionData(uid: String) {
        val idFilter = IdFilter.builder()
            .addDataUid(uid)
            .build()

        val deleteRequest = DataTypes.NUTRITION.deleteDataRequestBuilder
            .setIdFilter(idFilter)
            .build()

        /**  Make SDK call to delete nutrition data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            healthDataStore.deleteData(deleteRequest)
            _nutritionDeleteResponse.value = true
        }
    }

    fun resetUpdateResponse() {
        _nutritionUpdateResponse.value = false
    }

    fun resetDeleteResponse() {
        _nutritionDeleteResponse.value = false
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }
}
