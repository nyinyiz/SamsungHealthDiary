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
                
                // Color Logic
                val backgroundColor = when {
                    steps == 0L -> androidx.compose.ui.graphics.Color.Transparent
                    steps < 5000 -> androidx.compose.ui.graphics.Color(0xFFEF5350).copy(alpha = 0.3f) // Red for < 5k
                    steps < 10000 -> androidx.compose.ui.graphics.Color(0xFFFFCA28).copy(alpha = 0.3f) // Amber for 5k-10k
                    steps < 15000 -> androidx.compose.ui.graphics.Color(0xFF66BB6A).copy(alpha = 0.3f) // Green for 10k-15k
                    else -> androidx.compose.ui.graphics.Color(0xFF42A5F5).copy(alpha = 0.3f) // Blue for > 15k
                }
                
                val borderColor = when {
                    isToday -> ElectricBlue
                    else -> androidx.compose.ui.graphics.Color.Transparent
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(
                            width = if (isToday) 1.dp else 0.dp,
                            color = borderColor,
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
                            color = if (steps > 0 || isToday) TextPrimary else TextDisabled
                        )
                    }
                }
            }
        }
        
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = androidx.compose.ui.graphics.Color(0xFFEF5350), label = "< 5k")
            LegendItem(color = androidx.compose.ui.graphics.Color(0xFFFFCA28), label = "5k-10k")
            LegendItem(color = androidx.compose.ui.graphics.Color(0xFF66BB6A), label = "10k-15k")
            LegendItem(color = androidx.compose.ui.graphics.Color(0xFF42A5F5), label = "> 15k")
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}
