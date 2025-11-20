package com.samsung.android.health.sdk.sample.healthdiary.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.CheckPermissionUseCase
import com.samsung.android.health.sdk.sample.healthdiary.domain.usecase.RequestPermissionUseCase
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HealthMainViewModel @Inject constructor(
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val requestPermissionUseCase: RequestPermissionUseCase
) : ViewModel() {

    private val _permissionResponse = MutableStateFlow(Pair(AppConstants.WAITING, -1))
    val permissionResponse: StateFlow<Pair<String, Int>> = _permissionResponse.asStateFlow()

    private val _exceptionResponse = MutableStateFlow<Throwable?>(null)
    val exceptionResponse: StateFlow<Throwable?> = _exceptionResponse.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _exceptionResponse.emit(exception)
        }
    }

    fun checkForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        activityId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val hasPermission = checkPermissionUseCase(permSet)

            if (hasPermission) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                requestForPermission(context, permSet, activityId)
            }
        }
    }

    private fun requestForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        activityId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val activity = context as Activity
            val success = requestPermissionUseCase(permSet, activity)
            Log.i(TAG, "requestPermissions: Success $success")

            if (success) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.emit(Pair(AppConstants.NO_PERMISSION, -1))
                    Log.i(TAG, "requestPermissions: NO_PERMISSION")
                }
            }
        }
    }

    // Permissions for all data types accessed in this application
    fun connectToSamsungHealth(context: Context) {
        val permSet = setOf(
            Permission.of(DataTypes.STEPS, AccessType.READ),
            Permission.of(DataTypes.SLEEP, AccessType.READ),
            Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
            Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ),
            Permission.of(DataTypes.NUTRITION, AccessType.READ),
            Permission.of(DataTypes.HEART_RATE, AccessType.READ),
            Permission.of(DataTypes.NUTRITION, AccessType.WRITE)
        )
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            requestPermissionUseCase(permSet, context as Activity)
        }
    }

    fun resetPermissionResponse() {
        viewModelScope.launch {
            _permissionResponse.emit(Pair(AppConstants.WAITING, -1))
        }
    }

    fun setDefaultValueToExceptionResponse() {
        _exceptionResponse.value = null
    }

    companion object {
        private const val TAG = "[HTK]HealthDiaryViewModel"
    }
}
