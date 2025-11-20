package com.samsung.android.health.sdk.sample.healthdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.ChooseFoodScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.HeartRateScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.HealthMainScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.NutritionScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.SleepScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.StepScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.UpdateFoodScreen
import com.samsung.android.health.sdk.sample.healthdiary.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Step : Screen("step")
    data object HeartRate : Screen("heart_rate")
    data object Sleep : Screen("sleep")
    data object Nutrition : Screen("nutrition")
    data object Settings : Screen("settings")
    data object ChooseFood : Screen("choose_food/{mealType}/{insertDate}") {
        fun createRoute(mealType: Int, insertDate: String) = "choose_food/$mealType/$insertDate"
    }
    data object UpdateFood : Screen("update_food/{uid}/{mealType}/{insertDate}") {
        fun createRoute(uid: String, mealType: Int, insertDate: String) =
            "update_food/$uid/$mealType/$insertDate"
    }
}

@Composable
fun HealthDiaryNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier
    ) {
        composable(Screen.Main.route) {
            HealthMainScreen(navController = navController)
        }

        composable(Screen.Step.route) {
            StepScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.HeartRate.route) {
            HeartRateScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Sleep.route) {
            SleepScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Nutrition.route) {
            NutritionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChooseFood = { mealType, insertDate ->
                    navController.navigate(Screen.ChooseFood.createRoute(mealType, insertDate))
                },
                onNavigateToUpdateFood = { uid, mealType, insertDate ->
                    navController.navigate(Screen.UpdateFood.createRoute(uid, mealType, insertDate))
                }
            )
        }

        composable(
            route = Screen.ChooseFood.route,
            arguments = listOf(
                navArgument("mealType") { type = NavType.IntType },
                navArgument("insertDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mealType = backStackEntry.arguments?.getInt("mealType") ?: 0
            val insertDate = backStackEntry.arguments?.getString("insertDate") ?: ""
            ChooseFoodScreen(
                mealType = mealType,
                insertDate = insertDate,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.UpdateFood.route,
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("mealType") { type = NavType.IntType },
                navArgument("insertDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val mealType = backStackEntry.arguments?.getInt("mealType") ?: 0
            val insertDate = backStackEntry.arguments?.getString("insertDate") ?: ""
            UpdateFoodScreen(
                uid = uid,
                mealType = mealType,
                insertDate = insertDate,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
