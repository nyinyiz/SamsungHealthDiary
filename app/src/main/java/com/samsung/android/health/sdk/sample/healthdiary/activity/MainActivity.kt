/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.samsung.android.health.sdk.sample.healthdiary.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.samsung.android.health.sdk.sample.healthdiary.navigation.HealthDiaryNavigation
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.HealthDiaryTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: com.samsung.android.health.sdk.sample.healthdiary.viewmodel.SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            HealthDiaryTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    HealthDiaryNavigation(navController = navController)
                }
            }
        }
    }
}
