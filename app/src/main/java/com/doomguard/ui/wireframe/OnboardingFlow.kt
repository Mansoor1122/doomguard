package com.doomguard.ui.wireframe

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doomguard.ui.DoomViewModel
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.DoomGlassCard
import com.doomguard.ui.theme.doomCyberPrimaryFill
import com.doomguard.ui.theme.WireframeColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingFlow(
    vm: DoomViewModel,
    onFinished: () -> Unit,
) {
    val pager = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val notifyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DoomBrushes.hullVertical)
            .padding(24.dp),
    ) {
        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> OnboardPage(
                    icon = { Icon(Icons.Default.FavoriteBorder, null, Modifier.size(64.dp), tint = WireframeColors.Primary) },
                    title = "Understand. Improve. Reclaim.",
                    body = "Small steps to healthier scrolling with AI-powered clarity — welcome to DoomGuard.",
                )
                1 -> OnboardPage(
                    icon = { Icon(Icons.Default.WarningAmber, null, Modifier.size(64.dp), tint = WireframeColors.Warning) },
                    title = "Endless scrolling steals your time.",
                    body = "Feeds are tuned to keep you hooked. Seeing the pattern is the first move toward change.",
                )
                2 -> OnboardPage(
                    icon = { Icon(Icons.Default.Psychology, null, Modifier.size(64.dp), tint = WireframeColors.Primary) },
                    title = "AI-powered insights for a better you.",
                    body = "Google Gemini turns usage signals into coaching you'll actually use.",
                )
                3 -> PermissionsPage(
                    onOpenUsage = {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    },
                    onRequestNotify = {
                        if (Build.VERSION.SDK_INT >= 33) {
                            notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                )
                4 -> OnboardPage(
                    icon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            Modifier.size(72.dp),
                            tint = WireframeColors.Success,
                        )
                    },
                    title = "You're all set!",
                    body = "Grant usage access when you're ready for live stats — demo mode works instantly for pitches.",
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(5) { i ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(
                            if (pager.currentPage == i) WireframeColors.CyanBright else WireframeColors.Neutral.copy(alpha = 0.35f),
                            CircleShape,
                        ),
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        val last = pager.currentPage == 4
        Button(
            onClick = {
                if (last) {
                    vm.completeOnboarding { onFinished() }
                } else {
                    scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .doomCyberPrimaryFill(enabled = true),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (last) "Get started" else "Next",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 14.dp),
                )
            }
        }
        if (!last) {
            OutlinedButton(
                onClick = { vm.completeOnboarding { onFinished() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            WireframeColors.CyanGlow.copy(alpha = 0.65f),
                            WireframeColors.PurpleGlow.copy(alpha = 0.55f),
                        ),
                    ),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = WireframeColors.SurfaceElevated.copy(alpha = 0.75f),
                    contentColor = WireframeColors.TextPrimary,
                ),
            ) { Text("Skip tutorial", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun OnboardPage(
    icon: @Composable () -> Unit,
    title: String,
    body: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Spacer(Modifier.height(24.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = WireframeColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = WireframeColors.TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun PermissionsPage(
    onOpenUsage: () -> Unit,
    onRequestNotify: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Permissions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = WireframeColors.TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "DoomGuard works best with a few system permissions.",
            color = WireframeColors.TextSecondary,
        )
        Spacer(Modifier.height(20.dp))
        PermissionRow(title = "Usage access", subtitle = "Estimate social & scroll sessions", cta = "Open settings", onCta = onOpenUsage)
        Spacer(Modifier.height(12.dp))
        PermissionRow(title = "Notifications", subtitle = "Gentle wellness nudges", cta = "Allow", onCta = onRequestNotify)
        Spacer(Modifier.height(12.dp))
        PermissionRow(
            title = "Accessibility (optional)",
            subtitle = "Advanced gestures — not required for this MVP",
            cta = "Learn more",
            onCta = { /* optional */ },
        )
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    cta: String,
    onCta: () -> Unit,
) {
    DoomGlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp), elevation = 6.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = WireframeColors.Success, modifier = Modifier.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                Text(subtitle, fontSize = 13.sp, color = WireframeColors.TextSecondary)
            }
            OutlinedButton(
                onClick = onCta,
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(WireframeColors.CyanGlow.copy(alpha = 0.5f), WireframeColors.PurpleGlow.copy(alpha = 0.4f)),
                    ),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = WireframeColors.CyanBright,
                ),
            ) { Text(cta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}
