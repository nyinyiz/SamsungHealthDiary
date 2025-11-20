package com.samsung.android.health.sdk.sample.healthdiary.domain.model

import java.time.Instant

data class HeartRateData(
    val heartRate: Float,
    val minHeartRate: Float,
    val maxHeartRate: Float,
    val startTime: Instant,
    val endTime: Instant,
    val zoneOffset: java.time.ZoneOffset?
)
