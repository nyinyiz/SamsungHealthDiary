package com.samsung.android.health.sdk.sample.healthdiary.domain.usecase

import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.StepRepository
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for getting total step count
 * Simplified version for summary display
 */
class GetTotalStepsUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    
    suspend operator fun invoke(date: LocalDate): Result<Long> {
        Timber.d("GetTotalStepsUseCase: Fetching total steps for date: $date")
        
        return stepRepository.getTotalSteps(date)
            .onSuccess { count ->
                Timber.d("GetTotalStepsUseCase: Total steps: $count")
            }
            .onFailure { error ->
                Timber.e(error, "GetTotalStepsUseCase: Failed to fetch total steps")
            }
    }
}
