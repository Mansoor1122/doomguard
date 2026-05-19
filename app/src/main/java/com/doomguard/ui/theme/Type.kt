package com.doomguard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

private val DoomSans = FontFamily.SansSerif

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = (-0.75).sp,
        lineHeight = 40.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    headlineMedium = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.4).sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    headlineSmall = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = (-0.2).sp,
        lineHeight = 30.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    titleLarge = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = (-0.1).sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    titleMedium = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    bodyLarge = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Proportional,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    labelLarge = TextStyle(
        fontFamily = DoomSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.4.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
)
