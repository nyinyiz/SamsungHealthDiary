package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.android.health.sdk.sample.healthdiary.entries.FoodInfoTable
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.ChooseFoodViewModel
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseFoodScreen(
    mealType: Int,
    insertDate: String,
    onNavigateBack: () -> Unit,
    viewModel: ChooseFoodViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val insertResponse by viewModel.nutritionInsertResponse.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(insertResponse) {
        if (insertResponse) {
            showToast(context, "Food added successfully")
            viewModel.resetInsertResponse()
            onNavigateBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Food") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FoodInfoTable.keys().toList()) { foodName ->
                val foodInfo = FoodInfoTable[foodName]
                foodInfo?.let { info ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val dateTime = LocalDateTime.parse(insertDate)
                                val mealTypeEnum = when (mealType) {
                                    0 -> MealType.UNDEFINED
                                    1 -> MealType.BREAKFAST
                                    2 -> MealType.LUNCH
                                    3 -> MealType.DINNER
                                    4 -> MealType.MORNING_SNACK
                                    5 -> MealType.AFTERNOON_SNACK
                                    6 -> MealType.EVENING_SNACK
                                    else -> MealType.BREAKFAST
                                }

                                val sTime =
                                    dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
                                val eTime = sTime.plus(1, java.time.temporal.ChronoUnit.DAYS)

                                val nutritionData = HealthDataPoint.builder()
                                    .setStartTime(sTime)
                                    .setEndTime(eTime)
                                    .addFieldData(DataType.NutritionType.TITLE, foodName)
                                    .addFieldData(DataType.NutritionType.MEAL_TYPE, mealTypeEnum)
                                    .addFieldData(DataType.NutritionType.CALORIES, info.calories)
                                    .addFieldData(DataType.NutritionType.PROTEIN, info.protein)
                                    .addFieldData(DataType.NutritionType.TOTAL_FAT, info.totalFat)
                                    .addFieldData(
                                        DataType.NutritionType.SATURATED_FAT,
                                        info.saturatedFat
                                    )
                                    .addFieldData(
                                        DataType.NutritionType.POLYSATURATED_FAT,
                                        info.polySaturatedFat
                                    )
                                    .addFieldData(
                                        DataType.NutritionType.MONOSATURATED_FAT,
                                        info.monoSaturatedFat
                                    )
                                    .addFieldData(DataType.NutritionType.TRANS_FAT, info.transFat)
                                    .addFieldData(
                                        DataType.NutritionType.CARBOHYDRATE,
                                        info.carbohydrate
                                    )
                                    .addFieldData(
                                        DataType.NutritionType.DIETARY_FIBER,
                                        info.dietaryFiber
                                    )
                                    .addFieldData(DataType.NutritionType.SUGAR, info.sugar)
                                    .addFieldData(
                                        DataType.NutritionType.CHOLESTEROL,
                                        info.cholesterol
                                    )
                                    .addFieldData(DataType.NutritionType.SODIUM, info.sodium)
                                    .addFieldData(DataType.NutritionType.POTASSIUM, info.potassium)
                                    .addFieldData(DataType.NutritionType.VITAMIN_A, info.vitaminA)
                                    .addFieldData(DataType.NutritionType.VITAMIN_C, info.vitaminC)
                                    .addFieldData(DataType.NutritionType.CALCIUM, info.calcium)
                                    .addFieldData(DataType.NutritionType.IRON, info.iron)
                                    .build()

                                viewModel.insertNutritionData(nutritionData)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(foodName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${info.calories.toInt()} cal",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
