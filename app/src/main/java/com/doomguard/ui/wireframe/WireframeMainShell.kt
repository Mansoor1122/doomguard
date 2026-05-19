package com.doomguard.ui.wireframe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.doomguard.tracking.UsageStatsReader
import com.doomguard.ui.DoomViewModel
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.DoomGuardTheme
import com.doomguard.ui.theme.DoomThemeVariant
import com.doomguard.ui.theme.WireframeColors
import kotlinx.coroutines.delay

/** How often to re-query UsageStats while the main UI is visible (foreground). */
private const val USAGE_POLL_INTERVAL_MS = 15_000L

private sealed class WireTab(val route: String, val label: String) {
    data object Home : WireTab("whome", "Home")
    data object Insights : WireTab("winsights", "Insights")
    data object Coach : WireTab("wcoach", "Coach")
    data object Settings : WireTab("wsettings", "Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireframeMainShell(
    nav: NavHostController,
    vm: DoomViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                vm.refreshUsage()
                delay(USAGE_POLL_INTERVAL_MS)
            }
        }
    }

    var tab by rememberSaveable { mutableStateOf(WireTab.Home.route) }
    var nudgeDismissed by rememberSaveable { mutableStateOf(false) }

    val assessment by vm.assessment.collectAsStateWithLifecycle()
    val demo by vm.demoMode.collectAsStateWithLifecycle()
    val displayName by vm.displayName.collectAsStateWithLifecycle()
    val deviceSnapshot by vm.deviceSnapshot.collectAsStateWithLifecycle()

    val longest = assessment?.summary?.longestSessionMinutes ?: 0
    val showNudge = longest >= 45 && !nudgeDismissed && !demo

    DoomGuardTheme(variant = DoomThemeVariant.LightMain) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DoomBrushes.hullVertical),
        ) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "DoomGuard",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineSmall,
                                color = WireframeColors.TextPrimary,
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent,
                            scrolledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            titleContentColor = WireframeColors.TextPrimary,
                            navigationIconContentColor = WireframeColors.TextPrimary,
                            actionIconContentColor = WireframeColors.TextPrimary,
                        ),
                    )
                },
                bottomBar = {
                    FloatingCyberNavBar(
                        selectedRoute = tab,
                        onTab = { tab = it.route },
                        tabs = listOf(WireTab.Home, WireTab.Insights, WireTab.Coach, WireTab.Settings),
                    )
                },
            ) { pad ->
                Box(Modifier.padding(pad)) {
                    when (tab) {
                        WireTab.Home.route -> HomeDashboardTab(
                            displayName = displayName,
                            assessment = assessment,
                            demo = demo,
                            hasUsagePermission = UsageStatsReader.hasPermission(context),
                            deviceSnapshot = deviceSnapshot,
                        )
                        WireTab.Insights.route -> InsightsUsageTab(vm)
                        WireTab.Coach.route -> CoachTab(
                            vm = vm,
                            onAchievements = { nav.navigate("achievements") },
                            onStartFocus = { nav.navigate("focus") },
                        )
                        WireTab.Settings.route -> SettingsWireframeTab(vm)
                    }
                }
            }

            if (showNudge && assessment != null) {
                NudgeFullscreen(
                    minutes = longest.toInt(),
                    onTakeBreak = {
                        nudgeDismissed = true
                        nav.navigate("focus")
                    },
                    onContinue = { nudgeDismissed = true },
                )
            }
        }
    }
}

@Composable
private fun FloatingCyberNavBar(
    selectedRoute: String,
    onTab: (WireTab) -> Unit,
    tabs: List<WireTab>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 12.dp, top = 4.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .border(width = Dp.Hairline, brush = DoomBrushes.cardBorderGlow, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = WireframeColors.SurfaceElevated.copy(alpha = 0.88f),
            shadowElevation = 16.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { t ->
                    val selected = selectedRoute == t.route
                    val icon = when (t) {
                        WireTab.Home -> Icons.Default.Home
                        WireTab.Insights -> Icons.Default.BarChart
                        WireTab.Coach -> Icons.Default.AutoAwesome
                        WireTab.Settings -> Icons.Default.Settings
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onTab(t)
                            }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(44.dp),
                        ) {
                            if (selected) {
                                Box(
                                    Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    WireframeColors.CyanGlow.copy(alpha = 0.28f),
                                                    WireframeColors.PurpleGlow.copy(alpha = 0.1f),
                                                ),
                                            ),
                                        ),
                                )
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = t.label,
                                tint = if (selected) WireframeColors.CyanBright else WireframeColors.TextMuted,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) WireframeColors.TextPrimary else WireframeColors.TextMuted,
                            maxLines = 1,
                            modifier = Modifier.widthIn(max = 80.dp),
                        )
                    }
                }
            }
        }
    }
}
