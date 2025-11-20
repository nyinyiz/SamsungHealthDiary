package com.samsung.android.health.sdk.sample.healthdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeeklyStepChart(
    weeklyData: List<Pair<LocalDate, Long>>, // Must be 7 days, Sunday to Saturday
    maxSteps: Long,
    weekStartDate: LocalDate,
    weekEndDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    onJumpToToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite10)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with date range
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${weekStartDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${weekEndDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Jump to Today button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElectricBlue.copy(alpha = 0.2f))
                    .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onJumpToToday)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Today",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricBlue
                )
            }
        }

        // Bar Chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEach { (date, steps) ->
                val isFutureDay = date.isAfter(today)
                val isToday = date.isEqual(today)
                val heightFraction = if (maxSteps > 0 && !isFutureDay) steps.toFloat() / maxSteps.toFloat() else 0f
                val barHeight = (180 * heightFraction).coerceAtLeast(if (isFutureDay) 0f else 8f).dp

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Step count text
                    if (steps > 0 && !isFutureDay) {
                        Text(
                            text = if (steps >= 1000) "${steps / 1000}k" else steps.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Bar (empty for future days)
                    if (!isFutureDay && steps > 0) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = if (isToday) listOf(
                                            ElectricBlue,
                                            ElectricBlue.copy(alpha = 0.8f)
                                        ) else listOf(
                                            CyanGlow,
                                            CyanGlow.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                                .clickable { onDayClick(date) }
                        )
                    } else {
                        // Empty placeholder for future days or no data
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(GlassWhite10)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day label
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            .take(1),
                        fontSize = 12.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            isToday -> ElectricBlue
                            isFutureDay -> TextDisabled
                            else -> TextSecondary
                        }
                    )
                }
            }
        }
    }
}

