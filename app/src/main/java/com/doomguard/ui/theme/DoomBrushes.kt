package com.doomguard.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DoomBrushes {
    val hullVertical = Brush.verticalGradient(
        colors = listOf(WireframeColors.BackgroundLight, WireframeColors.BackgroundMid, WireframeColors.BackgroundDeep),
        startY = 0f,
        endY = 1600f,
    )

    val primaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            WireframeColors.PurpleNeon,
            WireframeColors.IndigoMid,
            WireframeColors.CyanBright,
        ),
    )

    val chartBar = Brush.verticalGradient(
        colors = listOf(WireframeColors.CyanGlow, WireframeColors.IndigoMid),
    )

    val cardBorderGlow = Brush.linearGradient(
        colors = listOf(
            WireframeColors.CyanGlow.copy(alpha = 0.95f),
            WireframeColors.PurpleGlow.copy(alpha = 0.85f),
            WireframeColors.CyanBright.copy(alpha = 0.6f),
        ),
    )

    /** Disabled / inactive gradient */
    @Stable
    fun primaryButtonMuted(): Brush = Brush.horizontalGradient(
        listOf(Color(0xFF3A455C), Color(0xFF2E3648)),
    )
}

private val DoomCardShape = RoundedCornerShape(22.dp)

@Composable
fun DoomGlassCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 10.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(DoomCardShape)
            .border(width = Dp.Hairline, brush = DoomBrushes.cardBorderGlow, shape = DoomCardShape),
        shape = DoomCardShape,
        color = WireframeColors.SurfaceLight.copy(alpha = 0.82f),
        shadowElevation = elevation,
        tonalElevation = 0.dp,
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

fun Modifier.doomCyberPrimaryFill(enabled: Boolean = true): Modifier {
    val bg = if (enabled) DoomBrushes.primaryHorizontal else DoomBrushes.primaryButtonMuted()
    return this
        .clip(RoundedCornerShape(28.dp))
        .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    WireframeColors.CyanGlow.copy(alpha = if (enabled) 0.65f else 0.35f),
                    WireframeColors.PurpleGlow.copy(alpha = if (enabled) 0.55f else 0.28f),
                ),
            ),
            shape = RoundedCornerShape(28.dp),
        )
        .background(bg, RoundedCornerShape(28.dp))
}
