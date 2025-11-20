package com.samsung.android.health.sdk.sample.healthdiary.domain.repository

import android.app.Activity
import com.samsung.android.sdk.health.data.permission.Permission

interface HealthConnectionRepository {
    suspend fun getGrantedPermissions(permissions: Set<Permission>): Set<Permission>
    suspend fun requestPermissions(permissions: Set<Permission>, activity: Activity): Set<Permission>
}
