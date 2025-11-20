package com.samsung.android.health.sdk.sample.healthdiary.data.repository

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.HeartRateData
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HeartRateRepository
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class HeartRateRepositoryImpl @Inject constructor(
    private val healthDataStore: HealthDataStore
) : HeartRateRepository {

    override suspend fun getHeartRateData(date: LocalDate): Result<List<HeartRateData>> {
        return try {
            Timber.d("HeartRateRepository: Fetching data for $date")
            val startDateTime = date.atStartOfDay()
            val endDateTime = date.plusDays(1).atStartOfDay()
            
            val localTimeFilter = LocalTimeFilter.of(startDateTime, endDateTime)
            val readRequest = DataTypes.HEART_RATE.readDataRequestBuilder
                .setLocalTimeFilter(localTimeFilter)
                .setOrdering(Ordering.DESC)
                .build()

            val response = healthDataStore.readData(readRequest)
            val dataList = response.dataList.map { it.toHeartRateData() }
            
            Timber.d("HeartRateRepository: Fetched ${dataList.size} records")
            Result.success(dataList)
        } catch (e: Exception) {
            Timber.e(e, "HeartRateRepository: Failed to fetch data")
            Result.failure(e)
        }
    }

    private fun HealthDataPoint.toHeartRateData(): HeartRateData {
        return HeartRateData(
            heartRate = this.getValue(DataType.HeartRateType.HEART_RATE) ?: 0f,
            minHeartRate = this.getValue(DataType.HeartRateType.MIN_HEART_RATE) ?: 0f,
            maxHeartRate = this.getValue(DataType.HeartRateType.MAX_HEART_RATE) ?: 0f,
            startTime = this.startTime ?: java.time.Instant.now(),
            endTime = this.endTime ?: java.time.Instant.now(),
            zoneOffset = this.zoneOffset
        )
    }
}
