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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.*

enum class ViewMode {
    DAY, WEEK, MONTH
}

@Composable
fun ViewModeToggle(
    selectedMode: ViewMode,
    onModeChange: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite10)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ViewMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) ElectricBlue.copy(alpha = 0.3f)
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onModeChange(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) ElectricBlue else TextSecondary
                )
            }
        }
    }
}
