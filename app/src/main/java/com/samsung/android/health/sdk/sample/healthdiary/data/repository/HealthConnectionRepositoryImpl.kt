package com.samsung.android.health.sdk.sample.healthdiary.data.repository

import android.app.Activity
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HealthConnectionRepository
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.permission.Permission
import javax.inject.Inject

class HealthConnectionRepositoryImpl @Inject constructor(
    private val healthDataStore: HealthDataStore
) : HealthConnectionRepository {

    override suspend fun getGrantedPermissions(permissions: Set<Permission>): Set<Permission> {
        return healthDataStore.getGrantedPermissions(permissions)
    }

    override suspend fun requestPermissions(permissions: Set<Permission>, activity: Activity): Set<Permission> {
        return healthDataStore.requestPermissions(permissions, activity)
    }
}
