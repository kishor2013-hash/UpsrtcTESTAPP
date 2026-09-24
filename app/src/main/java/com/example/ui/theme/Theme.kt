package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DarkNavyBg,
    primaryContainer = DarkNavyCard,
    onPrimaryContainer = CyanAccent,
    secondary = YellowAccent,
    onSecondary = DarkNavyBg,
    secondaryContainer = DarkNavyCard,
    onSecondaryContainer = YellowAccent,
    tertiary = GreenAccent,
    onTertiary = DarkNavyBg,
    background = DarkNavyBg,
    onBackground = TextWhite,
    surface = DarkNavySurface,
    onSurface = TextWhite,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextGray,
    outline = BorderDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark theme as required by the reference UI
    dynamicColor: Boolean = false, // Keep consistent brand colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
