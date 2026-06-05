package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GobPrimary,
    secondary = GobSecondary,
    tertiary = GobTertiary,
    background = Color(0xFF0F172A), // Dark slate background
    surface = Color(0xFF1E293B),    // Dark card slate
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
    primary = GobPrimary,
    secondary = GobSecondary,
    tertiary = GobTertiary,
    background = GobBackground,
    surface = GobSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GobTextPrimary,
    onSurface = GobTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamicColor to enforce our strict "Gobierno" civic branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
