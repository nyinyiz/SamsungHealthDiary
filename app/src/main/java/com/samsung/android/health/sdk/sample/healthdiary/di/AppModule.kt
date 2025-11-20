package com.samsung.android.health.sdk.sample.healthdiary.di

import android.content.Context
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.sdk.health.data.HealthDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHealthDataStore(
        @ApplicationContext context: Context
    ): HealthDataStore {
        return com.samsung.android.sdk.health.data.HealthDataService.getStore(context)
    }
}
