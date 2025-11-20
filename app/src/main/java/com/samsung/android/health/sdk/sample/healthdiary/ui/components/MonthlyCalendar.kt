package com.samsung.android.health.sdk.sample.healthdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun MonthlyCalendar(
    monthlyData: Map<LocalDate, Long>, // Map of date to step count
    selectedMonth: YearMonth,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = selectedMonth.atDay(1)
    val lastDayOfMonth = selectedMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = selectedMonth.lengthOfMonth()
    
    val maxSteps = monthlyData.values.maxOrNull() ?: 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite10)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${selectedMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        // Day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDisabled
                    )
                }
            }
        }

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Empty cells before first day
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Days of month
            items((1..daysInMonth).toList()) { day ->
                val date = selectedMonth.atDay(day)
                val steps = monthlyData[date] ?: 0L
                val isToday = date == LocalDate.now()
                val intensity = if (maxSteps > 0) steps.toFloat() / maxSteps.toFloat() else 0f

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            when {
                                isToday -> ElectricBlue.copy(alpha = 0.3f)
                                steps > 0 -> CyanGlow.copy(alpha = 0.2f * intensity)
                                else -> androidx.compose.ui.graphics.Color.Transparent
                            }
                        )
                        .border(
                            width = if (isToday) 1.dp else 0.dp,
                            color = if (isToday) ElectricBlue else androidx.compose.ui.graphics.Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onDayClick(date) }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = day.toString(),
                            fontSize = 14.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isToday -> ElectricBlue
                                steps > 0 -> TextPrimary
                                else -> TextDisabled
                            }
                        )
                        if (steps > 0) {
                            Text(
                                text = if (steps >= 1000) "${steps / 1000}k" else steps.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = CyanGlow
                            )
                        }
                    }
                }
            }
        }
    }
}
