package com.samsung.android.health.sdk.sample.healthdiary.domain.repository

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.HeartRateData
import java.time.LocalDate

interface HeartRateRepository {
    suspend fun getHeartRateData(date: LocalDate): Result<List<HeartRateData>>
}
