package com.samsung.android.health.sdk.sample.healthdiary.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    background = LightDeepBlack,
    surface = LightCosmicNavy,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.Black,
    background = DeepBlack,
    surface = CosmicNavy,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Immutable
data class HealthDiaryColors(
    val backgroundGradient: List<Color>,
    val glassBackground: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color
)

val LocalGradientColors = staticCompositionLocalOf {
    HealthDiaryColors(
        backgroundGradient = listOf(DeepBlack, CosmicNavy),
        glassBackground = GlassWhite10,
        glassBorder = GlassBorder,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textDisabled = TextDisabled
    )
}

@Composable
fun HealthDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val customColors = if (darkTheme) {
        HealthDiaryColors(
            backgroundGradient = listOf(DeepBlack, CosmicNavy),
            glassBackground = GlassWhite10,
            glassBorder = GlassBorder,
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            textDisabled = TextDisabled
        )
    } else {
        HealthDiaryColors(
            backgroundGradient = listOf(LightDeepBlack, LightCosmicNavy),
            glassBackground = LightGlassWhite10,
            glassBorder = LightGlassBorder,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textDisabled = LightTextDisabled
        )
    }

    CompositionLocalProvider(LocalGradientColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
