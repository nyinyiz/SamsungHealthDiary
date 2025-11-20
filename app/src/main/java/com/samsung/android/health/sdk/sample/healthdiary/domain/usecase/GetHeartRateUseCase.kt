package com.samsung.android.health.sdk.sample.healthdiary.domain.usecase

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.HeartRateData
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HeartRateRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHeartRateUseCase @Inject constructor(
    private val repository: HeartRateRepository
) {
    suspend operator fun invoke(date: LocalDate): Result<List<HeartRateData>> {
        return repository.getHeartRateData(date)
    }
}
