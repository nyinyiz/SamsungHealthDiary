package com.samsung.android.health.sdk.sample.healthdiary.domain.repository

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData
import java.time.LocalDate

/**
 * Repository interface for step data
 * Domain layer: defines contract, data layer implements
 */
interface StepRepository {
    
    /**
     * Get hourly step data for a specific date
     */
    suspend fun getStepData(date: LocalDate): Result<List<StepData>>
    
    /**
     * Get total step count for a specific date
     */
    suspend fun getTotalSteps(date: LocalDate): Result<Long>

    /**
     * Get daily step data for a range of dates
     */
    suspend fun getDailyStepsInRange(startDate: LocalDate, endDate: LocalDate): Result<List<StepData>>
}
