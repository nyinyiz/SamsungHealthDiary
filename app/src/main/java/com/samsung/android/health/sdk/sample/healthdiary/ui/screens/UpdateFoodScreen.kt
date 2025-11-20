package com.samsung.android.health.sdk.sample.healthdiary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samsung.android.health.sdk.sample.healthdiary.entries.FoodInfoTable
import com.samsung.android.health.sdk.sample.healthdiary.entries.NutritionUpdateData
import com.samsung.android.health.sdk.sample.healthdiary.utils.resolveException
import com.samsung.android.health.sdk.sample.healthdiary.utils.showToast
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.HealthViewModelFactory
import com.samsung.android.health.sdk.sample.healthdiary.viewmodel.UpdateFoodViewModel
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateFoodScreen(
    uid: String,
    mealType: Int,
    insertDate: String,
    onNavigateBack: () -> Unit,
    viewModel: UpdateFoodViewModel = viewModel(factory = HealthViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val updateResponse by viewModel.nutritionUpdateResponse.collectAsState()
    val deleteResponse by viewModel.nutritionDeleteResponse.collectAsState()
    val exceptionResponse by viewModel.exceptionResponse.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(exceptionResponse) {
        exceptionResponse?.let { exception ->
            showToast(context, exception.message ?: "Error occurred")
            resolveException(exception, context as android.app.Activity)
        }
    }

    LaunchedEffect(updateResponse) {
        if (updateResponse) {
            showToast(context, "Food updated successfully")
            viewModel.resetUpdateResponse()
            onNavigateBack()
        }
    }

    LaunchedEffect(deleteResponse) {
        if (deleteResponse) {
            showToast(context, "Food deleted successfully")
            viewModel.resetDeleteResponse()
            onNavigateBack()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setDefaultValueToExceptionResponse()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Food") },
            text = { Text("Are you sure you want to delete this food entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNutritionData(uid)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Food") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Update or delete this food entry",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Food Entry")
            }
        }
    }
}
