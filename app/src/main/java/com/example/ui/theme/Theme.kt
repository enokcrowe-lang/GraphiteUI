package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = GrapheneTextPrimary,
    onPrimary = AmoledBlack,
    secondary = GrapheneTextSecondary,
    onSecondary = AmoledBlack,
    tertiary = GrapheneAccentTeal,
    onTertiary = AmoledBlack,
    background = AmoledBlack,
    onBackground = GrapheneTextPrimary,
    surface = GrapheneDarkSurface,
    onSurface = GrapheneTextPrimary,
    surfaceVariant = GrapheneDarkCard,
    onSurfaceVariant = GrapheneTextSecondary,
    outline = GrapheneBorder,
    outlineVariant = GrapheneBorderLight
)

val LightColorScheme = lightColorScheme(
    primary = LightTextPrimary,
    onPrimary = Color.White,
    secondary = LightTextSecondary,
    onSecondary = Color.White,
    tertiary = GrapheneAccentBlue,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark privacy theme
    isTrueAmoled: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = if (darkTheme && isTrueAmoled) {
        baseScheme.copy(
            background = AmoledBlack,
            surface = AmoledBlack
        )
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
