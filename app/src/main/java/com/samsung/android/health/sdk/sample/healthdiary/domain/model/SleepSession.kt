package com.samsung.android.health.sdk.sample.healthdiary.domain.model

import java.time.LocalDateTime

data class SleepSession(
    val uid: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String? = null,
    val notes: String? = null,
    val duration: Long = 0 // in minutes
)
