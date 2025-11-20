package com.samsung.android.health.sdk.sample.healthdiary.domain.model

import java.time.Instant

/**
 * Domain model for step data
 * Clean architecture: independent of any framework or data source
 */
data class StepData(
    val startTime: Instant,
    val endTime: Instant,
    val count: Long
)
