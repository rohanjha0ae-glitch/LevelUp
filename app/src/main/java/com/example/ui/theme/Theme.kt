package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class LevelUpColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentGreen: Color,
    val accentBlue: Color,
    val accentRed: Color,
    val trackBg: Color,
    val isDark: Boolean
)

val LocalLevelUpColors = staticCompositionLocalOf {
    LevelUpColors(
        background = Color(0xFF0B0B0C),
        surface = Color(0xFF161618),
        surfaceVariant = Color(0xFF2C2C2E),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF636366),
        accentGreen = Color(0xFF30D158),
        accentBlue = Color(0xFF0A84FF),
        accentRed = Color(0xFFFF453A),
        trackBg = Color(0xFF1F1F1F),
        isDark = true
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = Black,
    secondary = OptimalGreen,
    onSecondary = Black,
    tertiary = SleepBlue,
    onTertiary = PureWhite,
    background = Black,
    onBackground = PureWhite,
    surface = DarkGrey,
    onSurface = PureWhite,
    surfaceVariant = MediumGrey,
    onSurfaceVariant = LightGrey,
    error = WorkoutRed
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = PureWhite,
    secondary = OptimalGreen,
    onSecondary = PureWhite,
    tertiary = SleepBlue,
    onTertiary = PureWhite,
    background = Color(0xFFF2F2F7),
    onBackground = Black,
    surface = PureWhite,
    onSurface = Black,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF636366),
    error = WorkoutRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to strictly maintain our requested color palette
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val levelUpColors = if (darkTheme) {
        LevelUpColors(
            background = Color(0xFF0B0B0C),
            surface = Color(0xFF161618),
            surfaceVariant = Color(0xFF2C2C2E),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF636366),
            accentGreen = Color(0xFF30D158),
            accentBlue = Color(0xFF0A84FF),
            accentRed = Color(0xFFFF453A),
            trackBg = Color(0xFF1F1F1F),
            isDark = true
        )
    } else {
        LevelUpColors(
            background = Color(0xFFF2F2F7),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE5E5EA),
            textPrimary = Color(0xFF1C1C1E),
            textSecondary = Color(0xFF8E8E93),
            accentGreen = Color(0xFF34C759),
            accentBlue = Color(0xFF007AFF),
            accentRed = Color(0xFFFF3B30),
            trackBg = Color(0xFFE5E5EA),
            isDark = false
        )
    }

    CompositionLocalProvider(LocalLevelUpColors provides levelUpColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
