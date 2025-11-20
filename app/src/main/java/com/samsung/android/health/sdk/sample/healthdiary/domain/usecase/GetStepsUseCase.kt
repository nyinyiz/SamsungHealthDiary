package com.samsung.android.health.sdk.sample.healthdiary.domain.usecase

import com.samsung.android.health.sdk.sample.healthdiary.domain.model.StepData
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.StepRepository
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for getting step data
 * Encapsulates business logic for retrieving step information
 */
class GetStepsUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    
    suspend operator fun invoke(date: LocalDate): Result<List<StepData>> {
        Timber.d("GetStepsUseCase: Fetching steps for date: $date")
        
        return stepRepository.getStepData(date)
            .onSuccess { data ->
                Timber.d("GetStepsUseCase: Successfully retrieved ${data.size} hourly records")
            }
            .onFailure { error ->
                Timber.e(error, "GetStepsUseCase: Failed to fetch steps")
            }
    }
}
