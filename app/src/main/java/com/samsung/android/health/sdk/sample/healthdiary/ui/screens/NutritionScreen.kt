package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.android.health.sdk.sample.healthdiary.ui.components.HealthDetailScaffold
import com.samsung.android.health.sdk.sample.healthdiary.utils.AppConstants
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.NutritionViewModel
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType

@Composable
fun NutritionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChooseFood: (Int, String) -> Unit,
    onNavigateToUpdateFood: (String, Int, String) -> Unit,
    viewModel: NutritionViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    var currentDate by remember { mutableStateOf(AppConstants.currentDate) }
    val dayStartTimeAsText by viewModel.dayStartTimeAsText.collectAsState()
    val totalCaloriesCount by viewModel.totalCaloriesCount.collectAsState()
    val dailyIntakeCalories by viewModel.dailyIntakeCaloriesData.collectAsState()
    val permissionResponse by viewModel.permissionResponse.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()
    var pendingMealType by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(permissionResponse) {
        if (permissionResponse != -1 && permissionResponse != AppConstants.NO_WRITE_PERMISSION) {
            pendingMealType?.let { mealType ->
                val dateString = currentDate.toString()
                onNavigateToChooseFood(mealType, dateString)
                pendingMealType = null
            }
            viewModel.resetPermissionResponse()
        } else if (permissionResponse == AppConstants.NO_WRITE_PERMISSION) {
            showToast(context, "Write permission not granted")
            viewModel.resetPermissionResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readNutritionData(currentDate)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

    HealthDetailScaffold(
        title = "Nutrition",
        dateText = dayStartTimeAsText,
        currentDate = currentDate,
        onNavigateBack = onNavigateBack,
        onDateSelected = { date ->
            currentDate = date
            viewModel.readNutritionData(date)
        },
        onPreviousDate = {
            if (currentDate > AppConstants.minimumDate) {
                currentDate = currentDate.minusDays(1)
                viewModel.readNutritionData(currentDate)
            }
        },
        onNextDate = {
            if (currentDate < AppConstants.currentDate) {
                currentDate = currentDate.plusDays(1)
                viewModel.readNutritionData(currentDate)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Calories", style = MaterialTheme.typography.titleMedium)
                    Text(
                        totalCaloriesCount,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MealCard(
                        mealType = "Breakfast",
                        mealTypeValue = MealType.BREAKFAST.ordinal,
                        calories = dailyIntakeCalories.breakfast,
                        onAddClick = {
                            pendingMealType = MealType.BREAKFAST.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.BREAKFAST] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.BREAKFAST.ordinal, dateString)
                        }
                    )
                }

                item {
                    MealCard(
                        mealType = "Lunch",
                        mealTypeValue = MealType.LUNCH.ordinal,
                        calories = dailyIntakeCalories.lunch,
                        onAddClick = {
                            pendingMealType = MealType.LUNCH.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.LUNCH] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.LUNCH.ordinal, dateString)
                        }
                    )
                }

                item {
                    MealCard(
                        mealType = "Dinner",
                        mealTypeValue = MealType.DINNER.ordinal,
                        calories = dailyIntakeCalories.dinner,
                        onAddClick = {
                            pendingMealType = MealType.DINNER.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.DINNER] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.DINNER.ordinal, dateString)
                        }
                    )
                }

                item {
                    MealCard(
                        mealType = "Morning Snack",
                        mealTypeValue = MealType.MORNING_SNACK.ordinal,
                        calories = dailyIntakeCalories.morningSnack,
                        onAddClick = {
                            pendingMealType = MealType.MORNING_SNACK.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.MORNING_SNACK] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.MORNING_SNACK.ordinal, dateString)
                        }
                    )
                }

                item {
                    MealCard(
                        mealType = "Afternoon Snack",
                        mealTypeValue = MealType.AFTERNOON_SNACK.ordinal,
                        calories = dailyIntakeCalories.afternoonSnack,
                        onAddClick = {
                            pendingMealType = MealType.AFTERNOON_SNACK.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.AFTERNOON_SNACK] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.AFTERNOON_SNACK.ordinal, dateString)
                        }
                    )
                }

                item {
                    MealCard(
                        mealType = "Evening Snack",
                        mealTypeValue = MealType.EVENING_SNACK.ordinal,
                        calories = dailyIntakeCalories.eveningSnack,
                        onAddClick = {
                            pendingMealType = MealType.EVENING_SNACK.ordinal
                            viewModel.requestWritePermission(context, AppConstants.CHOOSE_FOOD_ACTIVITY)
                        },
                        items = viewModel.nutritionTitleData[MealType.EVENING_SNACK] ?: arrayListOf(),
                        onItemClick = { uid ->
                            val dateString = currentDate.toString()
                            onNavigateToUpdateFood(uid, MealType.EVENING_SNACK.ordinal, dateString)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MealCard(
    mealType: String,
    mealTypeValue: Int,
    calories: Float,
    onAddClick: () -> Unit,
    items: List<com.samsung.android.health.sdk.sample.healthdiary.entries.NutritionUpdateData>,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(mealType, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${calories.toInt()} cal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add food")
                }
            }

            if (items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                items.forEach { nutritionData ->
                    TextButton(
                        onClick = { if (nutritionData.isClient) onItemClick(nutritionData.uid) },
                        enabled = nutritionData.isClient
                    ) {
                        Text("${nutritionData.title} (${nutritionData.amount.toInt()})")
                    }
                }
            }
        }
    }
}
