/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.entries.DailyIntakeCalories
import com.samsung.android.health.sdk.sample.healthdiary.entries.FoodInfoTable
import com.samsung.android.health.sdk.sample.healthdiary.entries.NutritionUpdateData
import com.samsung.android.health.sdk.sample.healthdiary.utils.dateFormat
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType.NutritionType
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import kotlinx.coroutines.CoroutineExceptionHandler
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionViewModel(private val healthDataStore: HealthDataStore) :
    ViewModel() {

    private val _permissionResponse = MutableStateFlow(-1)
    val permissionResponse: StateFlow<Int> = _permissionResponse.asStateFlow()

    private val _dailyIntakeCaloriesData = MutableStateFlow(DailyIntakeCalories())
    val dailyIntakeCaloriesData: StateFlow<DailyIntakeCalories> = _dailyIntakeCaloriesData.asStateFlow()

    private val _totalCaloriesCount = MutableStateFlow("0")
    val totalCaloriesCount: StateFlow<String> = _totalCaloriesCount.asStateFlow()

    private val _dayStartTimeAsText = MutableStateFlow("")
    val dayStartTimeAsText: StateFlow<String> = _dayStartTimeAsText.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    val nutritionTitleData = mutableMapOf<MealType, ArrayList<NutritionUpdateData>>()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun readNutritionData(dateTime: LocalDateTime) {
        _dayStartTimeAsText.value = dateTime.format(dateFormat)

        val readRequest = DataTypes.NUTRITION.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(dateTime, dateTime.plusDays(1)))
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read nutrition data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val intakeList = healthDataStore.readData(readRequest).dataList
            processReadDataResponse(intakeList)
        }
    }

    private fun processReadDataResponse(intakeList: List<HealthDataPoint>) {
        var totalCalories = 0f
        nutritionTitleData.clear()
        val dailyIntakeCalories = DailyIntakeCalories()

        intakeList.forEach { nutritionData ->
            totalCalories += nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F)
            val mealType =
                nutritionData.getValueOrDefault(NutritionType.MEAL_TYPE, MealType.BREAKFAST)
            val title = nutritionData.getValueOrDefault(NutritionType.TITLE, "")
            val calories = nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F)
            val amount = (calories / (FoodInfoTable[title]?.calories ?: 1F))

            dailyIntakeCalories.addData(
                mealType,
                nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F),
                title,
                nutritionData.getValueOrDefault(NutritionType.PROTEIN, 0F),
                nutritionData.getValueOrDefault(NutritionType.TOTAL_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.SATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.POLYSATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.MONOSATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.TRANS_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.CARBOHYDRATE, 0F),
                nutritionData.getValueOrDefault(NutritionType.DIETARY_FIBER, 0F),
                nutritionData.getValueOrDefault(NutritionType.SUGAR, 0F),
                nutritionData.getValueOrDefault(NutritionType.CHOLESTEROL, 0F),
                nutritionData.getValueOrDefault(NutritionType.SODIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.POTASSIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.VITAMIN_A, 0F),
                nutritionData.getValueOrDefault(NutritionType.VITAMIN_C, 0F),
                nutritionData.getValueOrDefault(NutritionType.CALCIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.IRON, 0F),
                calories
            )
            if ((nutritionData.dataSource?.appId ?: "") == AppConstants.APP_ID) {
                addNutritionParcel(
                    mealType,
                    title,
                    nutritionData.uid,
                    amount,
                    nutritionData.updateTime,
                    true
                )
            } else {
                addNutritionParcel(
                    mealType,
                    title,
                    nutritionData.uid,
                    amount,
                    nutritionData.updateTime,
                    false
                )
            }
        }

        _totalCaloriesCount.value = totalCalories.format()
        _dailyIntakeCaloriesData.value = dailyIntakeCalories
    }

    private fun addNutritionParcel(
        mealType: MealType,
        title: String,
        uid: String,
        amount: Float,
        updateTime: Instant?,
        isClient: Boolean
    ) {
        nutritionTitleData[mealType]?.add(
            NutritionUpdateData(title, uid, amount, updateTime, isClient)
        ) ?: run {
            nutritionTitleData[mealType] = arrayListOf(
                NutritionUpdateData(title, uid, amount, updateTime, isClient)
            )
        }
    }

    fun requestWritePermission(context: Context, appId: Int) {
        val permSet = mutableSetOf(
            Permission.of(DataTypes.NUTRITION, AccessType.WRITE)
        )

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val grantedPermissions = healthDataStore.getGrantedPermissions(permSet)

            if (grantedPermissions.containsAll(permSet)) {
                _permissionResponse.value = appId
            } else {
                requestForPermission(context, permSet, appId)
            }
        }
    }

    private fun requestForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        appId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val activity = context as Activity
            val result = healthDataStore.requestPermissions(permSet, activity)
            Log.i(TAG, "requestPermissions: Success ${result.size}")

            if (result.containsAll(permSet)) {
                _permissionResponse.value = appId
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.value = AppConstants.NO_WRITE_PERMISSION
                    Log.i(TAG, "requestPermissions: NO_PERMISSION")
                }
            }
        }
    }

    fun resetPermissionResponse() {
        _permissionResponse.value = -1
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }

    private fun Float.format() = String.format(Locale.ENGLISH, "%,d", this.toInt())

    companion object {
        private const val TAG = "[HD]NutritionViewModel"
    }
}
