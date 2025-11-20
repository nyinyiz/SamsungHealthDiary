package com.samsung.android.health.sdk.sample.healthdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.LocalGradientColors
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.GlassBorder
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.GlassWhite10
import com.samsung.android.health.sdk.sample.healthdiary.ui.theme.GlassWhite20

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(LocalGradientColors.current.glassBackground)
            .border(
                width = 1.dp,
                color = LocalGradientColors.current.glassBorder,
                shape = shape
            ),
        content = content
    )
}
