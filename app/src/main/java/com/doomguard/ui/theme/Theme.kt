package com.doomguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Immersive dark cyber hull — main app chrome */
private val CyberHullScheme = darkColorScheme(
    primary = WireframeColors.CyanBright,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2A3560),
    secondary = WireframeColors.PurpleGlow,
    onSecondary = Color.White,
    tertiary = WireframeColors.MintNeon,
    onTertiary = Color.Black,
    background = WireframeColors.BackgroundLight,
    onBackground = WireframeColors.TextPrimary,
    surface = WireframeColors.SurfaceLight,
    onSurface = WireframeColors.TextPrimary,
    surfaceVariant = WireframeColors.SurfaceElevated,
    onSurfaceVariant = WireframeColors.TextSecondary,
    outline = Color(0xFF3D4F7A),
    error = WireframeColors.Danger,
    onError = Color.White,
)

private val CyberFocusScheme = darkColorScheme(
    primary = WireframeColors.CyanGlow,
    onPrimary = Color.Black,
    secondary = WireframeColors.PurpleNeon,
    background = WireframeColors.FocusShellDark,
    onBackground = WireframeColors.TextPrimary,
    surface = WireframeColors.SurfaceLight,
    onSurface = WireframeColors.TextPrimary,
    surfaceVariant = WireframeColors.SurfaceElevated,
    onSurfaceVariant = WireframeColors.TextSecondary,
    error = WireframeColors.Danger,
    onError = Color.White,
)

enum class DoomThemeVariant {
    LightMain,
    DarkFocus,
}

@Composable
fun DoomGuardTheme(
    variant: DoomThemeVariant = DoomThemeVariant.LightMain,
    content: @Composable () -> Unit,
) {
    val scheme = when (variant) {
        DoomThemeVariant.LightMain -> CyberHullScheme
        DoomThemeVariant.DarkFocus -> CyberFocusScheme
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content,
    )
}
