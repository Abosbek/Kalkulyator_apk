package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CalculatorColors(
    val canvas: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val keyNumber: Color,
    val keyNumberText: Color,
    val keyOperator: Color,
    val keyOperatorText: Color,
    val keyFunction: Color,
    val keyFunctionText: Color,
    val keyEquals: Color,
    val keyEqualsText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val accentPrimary: Color,
    val isDark: Boolean
)

val LocalCalculatorColors = staticCompositionLocalOf {
    CalculatorColors(
        canvas = DarkCanvas,
        surfaceCard = DarkSurfaceCard,
        surfaceElevated = DarkSurfaceElevated,
        keyNumber = DarkKeyNumber,
        keyNumberText = DarkKeyNumberText,
        keyOperator = DarkKeyOperator,
        keyOperatorText = DarkKeyOperatorText,
        keyFunction = DarkKeyFunction,
        keyFunctionText = DarkKeyFunctionText,
        keyEquals = DarkKeyEquals,
        keyEqualsText = Color.White,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        divider = DarkDivider,
        accentPrimary = AccentBlue,
        isDark = true
    )
}

val CustomDarkColors = CalculatorColors(
    canvas = DarkCanvas,
    surfaceCard = DarkSurfaceCard,
    surfaceElevated = DarkSurfaceElevated,
    keyNumber = DarkKeyNumber,
    keyNumberText = DarkKeyNumberText,
    keyOperator = DarkKeyOperator,
    keyOperatorText = DarkKeyOperatorText,
    keyFunction = DarkKeyFunction,
    keyFunctionText = DarkKeyFunctionText,
    keyEquals = DarkKeyEquals,
    keyEqualsText = Color.White,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    divider = DarkDivider,
    accentPrimary = AccentBlue,
    isDark = true
)

val CustomOledDarkColors = CalculatorColors(
    canvas = DarkOledCanvas,
    surfaceCard = Color(0xFF141416),
    surfaceElevated = Color(0xFF1C1D22),
    keyNumber = Color(0xFF1E2028),
    keyNumberText = Color(0xFFF3F4F8),
    keyOperator = DarkKeyOperator,
    keyOperatorText = Color.White,
    keyFunction = Color(0xFF2C2F3C),
    keyFunctionText = Color(0xFFD0D5E4),
    keyEquals = DarkKeyEquals,
    keyEqualsText = Color.White,
    textPrimary = Color.White,
    textSecondary = Color(0xFF8E95A8),
    divider = Color(0xFF20222B),
    accentPrimary = AccentBlue,
    isDark = true
)

val CustomLightColors = CalculatorColors(
    canvas = LightCanvas,
    surfaceCard = LightSurfaceCard,
    surfaceElevated = LightSurfaceElevated,
    keyNumber = LightKeyNumber,
    keyNumberText = LightKeyNumberText,
    keyOperator = LightKeyOperator,
    keyOperatorText = LightKeyOperatorText,
    keyFunction = LightKeyFunction,
    keyFunctionText = LightKeyFunctionText,
    keyEquals = LightKeyEquals,
    keyEqualsText = Color.White,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    divider = LightDivider,
    accentPrimary = AccentBlue,
    isDark = false
)

private val M3DarkScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentCyan,
    tertiary = AccentAmber,
    background = DarkCanvas,
    surface = DarkSurfaceCard
)

private val M3LightScheme = lightColorScheme(
    primary = AccentBlue,
    secondary = AccentCyan,
    tertiary = AccentAmber,
    background = LightCanvas,
    surface = LightSurfaceCard
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = true,
    oledBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    val customColors = when {
        !darkTheme -> CustomLightColors
        oledBlack -> CustomOledDarkColors
        else -> CustomDarkColors
    }

    val m3Scheme = if (darkTheme) M3DarkScheme else M3LightScheme

    CompositionLocalProvider(LocalCalculatorColors provides customColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography = Typography,
            content = content
        )
    }
}
