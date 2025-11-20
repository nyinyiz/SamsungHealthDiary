package com.samsung.android.health.sdk.sample.healthdiary.di

import com.samsung.android.health.sdk.sample.healthdiary.data.repository.HealthConnectionRepositoryImpl
import com.samsung.android.health.sdk.sample.healthdiary.data.repository.HeartRateRepositoryImpl
import com.samsung.android.health.sdk.sample.healthdiary.data.repository.StepRepositoryImpl
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HealthConnectionRepository
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.HeartRateRepository
import com.samsung.android.health.sdk.sample.healthdiary.domain.repository.StepRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(
        impl: StepRepositoryImpl
    ): StepRepository

    @Binds
    @Singleton
    abstract fun bindHealthConnectionRepository(
        impl: HealthConnectionRepositoryImpl
    ): HealthConnectionRepository

    @Binds
    @Singleton
    abstract fun bindHeartRateRepository(
        impl: HeartRateRepositoryImpl
    ): HeartRateRepository
}
