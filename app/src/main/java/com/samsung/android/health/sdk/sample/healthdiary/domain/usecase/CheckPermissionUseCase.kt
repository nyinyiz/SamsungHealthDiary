package com.samsung.android.health.sdk.sample.healthdiary.domain.usecase

import android.app.Activity
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HealthConnectionRepository
import com.samsung.android.sdk.health.data.permission.Permission
import javax.inject.Inject

class CheckPermissionUseCase @Inject constructor(
    private val repository: HealthConnectionRepository
) {
    suspend operator fun invoke(permissions: Set<Permission>): Boolean {
        val granted = repository.getGrantedPermissions(permissions)
        return granted.containsAll(permissions)
    }
}
