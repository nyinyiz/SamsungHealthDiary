package com.samsung.android.health.sdk.sample.healthdiary.data.repository

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.StepRepository
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import com.samsung.android.sdk.health.data.request.Ordering
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Implementation of StepRepository
 * Wraps Samsung Health SDK data access
 */
class StepRepositoryImpl @Inject constructor(
    private val healthDataStore: HealthDataStore
) : StepRepository {

    override suspend fun getStepData(date: LocalDate): Result<List<StepData>> {
        return try {
            Timber.d("StepRepository: Fetching step data for $date")
            
            val startDateTime = date.atStartOfDay()
            val endDateTime = date.plusDays(1).atStartOfDay()
            
            val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
            val localTimeGroup = LocalTimeGroup.of(LocalTimeGroupUnit.HOURLY, 1)
            
            val aggregateRequest = DataType.StepsType.TOTAL.requestBuilder
                .setLocalTimeFilterWithGroup(localTimeFilter, localTimeGroup)
                .setOrdering(Ordering.ASC)
                .build()

            val response = healthDataStore.aggregateData(aggregateRequest)
            
            val stepDataList = response.dataList.map { it.toStepData() }
            
            Timber.d("StepRepository: Successfully fetched ${stepDataList.size} hourly records")
            Result.success(stepDataList)
            
        } catch (e: Exception) {
            Timber.e(e, "StepRepository: Failed to fetch step data")
            Result.failure(e)
        }
    }

    override suspend fun getTotalSteps(date: LocalDate): Result<Long> {
        return try {
            Timber.d("StepRepository: Fetching total steps for $date")
            
            val stepDataResult = getStepData(date)
            
            if (stepDataResult.isSuccess) {
                val total = stepDataResult.getOrNull()?.sumOf { it.count } ?: 0L
                Timber.d("StepRepository: Total steps = $total")
                Result.success(total)
            } else {
                Result.failure(stepDataResult.exceptionOrNull() ?: Exception("Unknown error"))
            }
            
        } catch (e: Exception) {
            Timber.e(e, "StepRepository: Failed to fetch total steps")
            Result.failure(e)
        }
    }

    override suspend fun getDailyStepsInRange(startDate: LocalDate, endDate: LocalDate): Result<List<StepData>> {
        return try {
            Timber.d("StepRepository: Fetching daily steps from $startDate to $endDate")
            
            val startDateTime = startDate.atStartOfDay()
            val endDateTime = endDate.plusDays(1).atStartOfDay()
            
            val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
            val localTimeGroup = LocalTimeGroup.of(LocalTimeGroupUnit.DAILY, 1)
            
            val aggregateRequest = DataType.StepsType.TOTAL.requestBuilder
                .setLocalTimeFilterWithGroup(localTimeFilter, localTimeGroup)
                .setOrdering(Ordering.ASC)
                .build()

            val response = healthDataStore.aggregateData(aggregateRequest)
            
            val stepDataList = response.dataList.map { it.toStepData() }
            
            Timber.d("StepRepository: Successfully fetched ${stepDataList.size} daily records")
            Result.success(stepDataList)
            
        } catch (e: Exception) {
            Timber.e(e, "StepRepository: Failed to fetch daily steps range")
            Result.failure(e)
        }
    }

    /**
     * Extension function to convert SDK AggregatedData to domain StepData
     */
    private fun AggregatedData<Long>.toStepData(): StepData {
        return StepData(
            startTime = this.startTime,
            endTime = this.endTime,
            count = this.value ?: 0L
        )
    }
}
