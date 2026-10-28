package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

// CompositionLocal to track if dark mode is active
val LocalIsDark = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),      // Neon Green
    secondary = Color(0xFFB71C1C),    // Dark Red
    tertiary = Color(0xFF34D399),     // Mint Green
    background = Color(0xFF070B09),   // Obsidian dark green-black background
    surface = Color(0xFF0F1613),      // Dark forest-glass card
    onPrimary = Color(0xFF070B09),    // Dark text on neon green for high readability
    onSecondary = Color.White,
    onBackground = Color(0xFFFFFFFF), // White text
    onSurface = Color(0xFFFFFFFF)     // White text
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),      // Vibrant Blue
    secondary = Color(0xFFB71C1C),    // Dark Red
    tertiary = Color(0xFF2563EB),     // Active Blue
    background = Color(0xFFF1F5F9),   // Ice white background
    surface = Color(0xFFFFFFFF),      // Pure White cards
    onPrimary = Color.White,          // White text on blue for high readability
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A), // Slate 900 text
    onSurface = Color(0xFF0F172A)     // Slate 900 text
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    androidx.compose.runtime.CompositionLocalProvider(LocalIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
