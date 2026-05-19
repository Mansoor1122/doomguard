package com.doomguard.ui.theme

import androidx.compose.ui.graphics.Color

/** Cyber–AI DoomGuard branding (dark-first). Existing names reused app-wide for minimal churn */
object WireframeColors {
    // ── Neon spectrum ───────────────────────────────
    val PurpleNeon = Color(0xFF7B61FF)
    val IndigoMid = Color(0xFF5B5CFF)
    val CyanBright = Color(0xFF00D2FF)
    val MintNeon = Color(0xFF00FFC6)
    val CyanGlow = Color(0xFF00E5FF)
    val PurpleGlow = Color(0xFF8A5CFF)

    /** Maps to cyan accent everywhere we used Primary before */
    val Primary = CyanBright
    val Success = Color(0xFF00FFB3)
    val Warning = Color(0xFFFFB547)
    val Danger = Color(0xFFFF5E7A)
    val Neutral = Color(0xFF7C86A8)

    // ── Surfaces ────────────────────────────────────
    val BackgroundDeep = Color(0xFF050816)
    val BackgroundMid = Color(0xFF0B1020)

    /** Top-level scaffold (was “light”; now abyss hull) */
    val BackgroundLight = BackgroundDeep

    /** Glass cards */
    val SurfaceLight = Color(0xFF12182B)
    val SurfaceElevated = Color(0xFF1A2238)

    // ── Text ────────────────────────────────────────
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB8C1D9)
    val TextMuted = Neutral

    // ── Shells / overlays ────────────────────────────
    val FocusShellDark = Color(0xFF050816)
    val NudgeDark = Color(0xFF070B17)

    /** Legacy gradient strip (splash / badges) */
    val GradientRibbonStart = PurpleNeon
    val GradientRibbonEnd = CyanBright
}
