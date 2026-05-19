package com.doomguard.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.doomguard.R
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.WireframeColors
import com.doomguard.ui.wireframe.WireframeRoot
import kotlinx.coroutines.delay

private const val SPLASH_HOLD_MS = 1_850L

@Composable
fun DoomRoot() {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(SPLASH_HOLD_MS)
        showSplash = false
    }

    if (showSplash) {
        SplashLogoScreen()
    } else {
        WireframeRoot()
    }
}

@Composable
private fun SplashLogoScreen() {
    val infinite = rememberInfiniteTransition(label = "logo_pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Reverse),
        label = "scale",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DoomBrushes.hullVertical),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WireframeColors.PurpleGlow.copy(alpha = 0.28f),
                            Color.Transparent,
                        ),
                        radius = 900f,
                    ),
                ),
        )
        Image(
            painter = painterResource(id = R.drawable.ic_doomguard_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 40.dp)
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                    transformOrigin = TransformOrigin.Center
                },
            contentScale = ContentScale.Fit,
        )
    }
}
