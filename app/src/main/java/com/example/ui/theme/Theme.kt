package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HealthPrimary,
    secondary = HealthSecondary,
    tertiary = HealthTertiary,
    background = Color(0xFF1B5E20),
    surface = Color(0xFF2E7D32),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = HealthPrimary,
    secondary = HealthSecondary,
    tertiary = HealthTertiary,
    background = HealthBackground,
    surface = HealthSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = HealthTextPrimary,
    onSurface = HealthTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
