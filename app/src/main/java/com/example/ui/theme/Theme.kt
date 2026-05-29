package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    secondary = CyberPurple,
    tertiary = CyberGreen,
    background = CyberDarkBg,
    surface = CyberSurface,
    surfaceVariant = CyberSurfaceVariant,
    onPrimary = Color(0xFF020617),
    onSecondary = Color.White,
    onTertiary = Color(0xFF02040A),
    onBackground = TextCelestial,
    onSurface = TextCelestial,
    onSurfaceVariant = TextCelestial
)

private val LightColorScheme = lightColorScheme(
    primary = CyberPurple,
    secondary = CyberCyan,
    tertiary = CyberGreen,
    background = CyberLightBg,
    surface = CyberLightSurface,
    onPrimary = Color.White,
    onSecondary = Color(0xFF020617),
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Explicitly enforce our custom beautiful theme color scheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
