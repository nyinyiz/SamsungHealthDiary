package com.samsung.android.health.sdk.sample.healthdiary.domain.usecase

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.StepRepository
import java.time.LocalDate
import javax.inject.Inject

class GetWeeklyStepsUseCase @Inject constructor(
    private val repository: StepRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): Result<List<StepData>> {
        return repository.getDailyStepsInRange(startDate, endDate)
    }
}
