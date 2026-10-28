package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset

// CompositionLocal to track if dark mode is active (defined in Theme.kt)

// Premium Vibrant Organic Grocery Color Scheme (Behance Dark / Specific Light Palette)
val SlateDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF070B09) else Color(0xFFF1F5F9) // Dark: Obsidian Green, Light: Ice White

val CardDarkBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF0F1613) else Color(0xFFFFFFFF) // Dark: Forest-glass card, Light: Pure White

val PrimaryCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF10B981) else Color(0xFF1D4ED8) // Dark: Neon green, Light: Azure Blue

val SecondaryMint: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF34D399) else Color(0xFF2563EB) // Dark: Mint, Light: Vibrant blue

val HeaderBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF10B981) else Color(0xFF1D4ED8)

val AccentCoral: Color
    @Composable
    @ReadOnlyComposable
    get() = Color(0xFFF43F5E) // Beautiful coral red

val WarmAmbar: Color
    @Composable
    @ReadOnlyComposable
    get() = Color(0xFFF59E0B) // Amber

val SoftGrayText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFF94A3B8) else Color(0xFF64748B) // Slate 400 / Slate 500

val PolarLight: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsDark.current) Color(0xFFFFFFFF) else Color(0xFF0F172A) // Dark: Polar white, Light: Slate 900

val ButtonGlowWhite: Color = Color(0xFFFFFFFF)

val ButtonGlowShadow: Shadow
    @Composable
    @ReadOnlyComposable
    get() = if (!LocalIsDark.current) {
        Shadow(
            color = Color(0xFFFFFFFF), // Stronger bright glowing white shadow in Light Mode
            offset = Offset(0f, 0f),
            blurRadius = 16f
        )
    } else {
        Shadow(
            color = Color(0x88FFFFFF),
            offset = Offset(0f, 0f),
            blurRadius = 8f
        )
    }

val Purple80 = Color(0xFFB4E3C5)
val PurpleGrey80 = Color(0xFFC2DCD2)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF53B175)
val PurpleGrey40 = Color(0xFFF9FAFB)
val Pink40 = Color(0xFFF23D5F)



