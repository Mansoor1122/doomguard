package com.doomguard.ui.wireframe

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doomguard.ui.DoomViewModel
import com.doomguard.ui.theme.doomCyberPrimaryFill
import com.doomguard.ui.theme.WireframeColors

@Composable
fun InsightsUsageTab(
    vm: DoomViewModel,
) {
    val period by vm.usagePeriod.collectAsStateWithLifecycle()
    val hourly by vm.hourly.collectAsStateWithLifecycle()
    val weekly by vm.weekly.collectAsStateWithLifecycle()
    val topApps by vm.topApps.collectAsStateWithLifecycle()
    val deviceSnapshot by vm.deviceSnapshot.collectAsStateWithLifecycle()
    val demo by vm.demoMode.collectAsStateWithLifecycle()
    val tabIdx = when (period) {
        DoomViewModel.UsagePeriod.Day -> 0
        DoomViewModel.UsagePeriod.Week -> 1
        DoomViewModel.UsagePeriod.Month -> 2
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 100.dp),
    ) {
        item {
            Text("Usage", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
            Text("Breakdown by time and top apps", color = WireframeColors.TextSecondary, fontSize = 14.sp)
        }
        item {
            TabRow(
                selectedTabIndex = tabIdx,
                containerColor = Color.Transparent,
                contentColor = WireframeColors.CyanBright,
            ) {
                Tab(
                    selected = period == DoomViewModel.UsagePeriod.Day,
                    onClick = { vm.setUsagePeriod(DoomViewModel.UsagePeriod.Day) },
                    text = { Text("Day") },
                )
                Tab(
                    selected = period == DoomViewModel.UsagePeriod.Week,
                    onClick = { vm.setUsagePeriod(DoomViewModel.UsagePeriod.Week) },
                    text = { Text("Week") },
                )
                Tab(
                    selected = period == DoomViewModel.UsagePeriod.Month,
                    onClick = { vm.setUsagePeriod(DoomViewModel.UsagePeriod.Month) },
                    text = { Text("Month") },
                )
            }
        }
        item {
            WireframeSectionCard {
                Column {
                    when (period) {
                        DoomViewModel.UsagePeriod.Day -> {
                            Text("Today (by hour)", fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            DayHourUsageChart(hourly)
                        }
                        DoomViewModel.UsagePeriod.Week -> {
                            Text("Last 7 days", fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            WeekTrendLineChart(weekly)
                        }
                        DoomViewModel.UsagePeriod.Month -> {
                            Text("Month (weekly buckets)", fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            val monthBars = List(4) { i -> weekly.getOrElse(i * 2) { 0.5f } }
                            MonthWeekBarsChart(monthBars)
                        }
                    }
                }
            }
        }
        item {
            WireframeSectionCard {
                Column {
                    Text("Weekly average", fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                    val avg = if (weekly.isEmpty()) 0 else (weekly.sum() / weekly.size * 100).toInt()
                    Text("$avg doom-index (demo-normalized)", color = WireframeColors.TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Highlights", fontWeight = FontWeight.SemiBold)
                    val best = weekly.indices.maxByOrNull { weekly[it] } ?: 0
                    Text("• Best day: ${listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[best]}", fontSize = 14.sp, color = WireframeColors.TextSecondary)
                }
            }
        }
        item {
            WireframeSectionCard {
                Column {
                    Text("Top apps", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    TopAppsList(topApps)
                }
            }
        }
        deviceSnapshot?.let { snap ->
            item {
                WireframeSectionCard {
                    Column {
                        Text("All apps today", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
                        Text(
                            if (demo) "Demo snapshot · totals across your device today"
                            else "From usage stats · foreground time today",
                            color = WireframeColors.TextSecondary,
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${snap.totalScreenMinutesToday} min on screen · ${snap.distinctPackagesUsedToday} apps used",
                            fontSize = 14.sp,
                            color = WireframeColors.TextSecondary,
                        )
                        snap.approximateInstalledUserApps?.let { n ->
                            Text("~$n user apps installed", fontSize = 13.sp, color = WireframeColors.TextSecondary)
                        }
                        if (snap.topApps.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            AllAppsTodayUsageRows(entries = snap.topApps, maxItems = 45)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoachTab(
    vm: DoomViewModel,
    onAchievements: () -> Unit,
    onStartFocus: () -> Unit,
) {
    val filter by vm.coachFilter.collectAsStateWithLifecycle()
    val aiText by vm.aiText.collectAsStateWithLifecycle()
    val loading by vm.aiLoading.collectAsStateWithLifecycle()
    val err by vm.aiError.collectAsStateWithLifecycle()
    val graphics by vm.wellbeingGraphics.collectAsStateWithLifecycle()
    val graphicsLoading by vm.graphicsLoading.collectAsStateWithLifecycle()
    val graphicsErr by vm.graphicsError.collectAsStateWithLifecycle()
    val history by vm.history.collectAsStateWithLifecycle()
    if (err != null) {
        AlertDialog(
            onDismissRequest = { vm.clearAiError() },
            confirmButton = { TextButton(onClick = { vm.clearAiError() }) { Text("OK") } },
            title = { Text("Gemini") },
            text = { Text(err!!) },
        )
    }
    if (graphicsErr != null) {
        AlertDialog(
            onDismissRequest = { vm.clearGraphicsError() },
            confirmButton = { TextButton(onClick = { vm.clearGraphicsError() }) { Text("OK") } },
            title = { Text("Visual insight") },
            text = { Text(graphicsErr!!) },
        )
    }
    val recs = CoachLibrary.all.filter { rec ->
        when (filter) {
            CoachFilterChip.All -> true
            else -> rec.tags.contains(filter)
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 100.dp),
    ) {
        item {
            CoachHeader(onAchievements, onStartFocus)
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WireframeColors.PurpleGlow)
                Text(
                    "Powered by Google Gemini",
                    fontWeight = FontWeight.SemiBold,
                    color = WireframeColors.CyanGlow,
                    fontSize = 14.sp,
                )
            }
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    CoachFilterChip.All,
                    CoachFilterChip.Focus,
                    CoachFilterChip.Sleep,
                    CoachFilterChip.Habits,
                ).forEach { chip ->
                    val sel = filter == chip
                    FilterChip(
                        selected = sel,
                        onClick = { vm.setCoachFilter(chip) },
                        label = { Text(chip.name, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium) },
                        border = BorderStroke(
                            1.dp,
                            if (sel) WireframeColors.CyanGlow.copy(alpha = 0.65f)
                            else WireframeColors.Neutral.copy(alpha = 0.28f),
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WireframeColors.CyanGlow.copy(alpha = 0.22f),
                            selectedLabelColor = WireframeColors.TextPrimary,
                            selectedLeadingIconColor = WireframeColors.CyanBright,
                            labelColor = WireframeColors.TextSecondary,
                            containerColor = WireframeColors.SurfaceLight.copy(alpha = 0.42f),
                        ),
                    )
                }
            }
        }
        item {
            WireframeSectionCard {
                Column {
                    Text("Today’s insight", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    when {
                        loading -> CoachLoadingRow()
                        aiText != null -> Text(aiText!!, color = WireframeColors.TextSecondary, lineHeight = 22.sp)
                        else -> Text("Generate a fresh coaching brief from your latest doomscore.", color = WireframeColors.TextSecondary)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.generateAiInsight() },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = WireframeColors.TextPrimary),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .doomCyberPrimaryFill(enabled = !loading),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(if (loading) "Thinking…" else "Refresh insight", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 14.dp))
                        }
                    }
                }
            }
        }
        item {
            WireframeSectionCard {
                Column {
                    Text("Visual wellbeing", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Sends today’s app list and usage to Gemini; you get meters and charts, not a long essay.",
                        color = WireframeColors.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    when {
                        graphicsLoading -> CoachLoadingRow()
                        graphics != null -> WellbeingGraphicsPanel(graphics!!)
                        else -> Text(
                            "Tap below to build a chart-style snapshot from your latest data.",
                            color = WireframeColors.TextSecondary,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { vm.generateVisualWellbeing() },
                        enabled = !graphicsLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
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
                    ) {
                        Text(
                            when {
                                graphicsLoading -> "Building…"
                                graphics != null -> "Regenerate visuals"
                                else -> "Generate visuals"
                            },
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
        items(recs, key = { it.id }) { r ->
            WireframeSectionCard {
                Column {
                    Text(r.title, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(r.body, fontSize = 14.sp, color = WireframeColors.TextSecondary, lineHeight = 20.sp)
                }
            }
        }
        item {
            Text("Past reports", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
        }
        if (history.isEmpty()) {
            item { Text("No saved reports yet.", color = WireframeColors.TextSecondary, fontSize = 14.sp) }
        } else {
            items(history, key = { it.dayEpochDay }) { h ->
                WireframeSectionCard {
                    Text("${h.mostUsedAppLabel} · score ${h.doomScore}", fontWeight = FontWeight.Medium)
                    Text(h.aiText.take(120) + if (h.aiText.length > 120) "…" else "", fontSize = 13.sp, color = WireframeColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun CoachHeader(onAchievements: () -> Unit, onFocus: () -> Unit) {
    Column {
        Text("Coach", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoachNavOutlineButton(onClick = onAchievements, label = "Achievements")
            CoachNavOutlineButton(onClick = onFocus, label = "Focus mode")
        }
    }
}

@Composable
private fun CoachNavOutlineButton(onClick: () -> Unit, label: String) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(46.dp),
        shape = RoundedCornerShape(24.dp),
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
    ) { Text(label, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun CoachLoadingRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = WireframeColors.CyanBright,
            trackColor = WireframeColors.SurfaceLight.copy(alpha = 0.38f),
            strokeWidth = 2.dp,
        )
        Text("Gemini is thinking…", color = WireframeColors.TextSecondary)
    }
}

@Composable
fun SettingsWireframeTab(vm: DoomViewModel) {
    val demo by vm.demoMode.collectAsStateWithLifecycle()
    val notifications by vm.notificationsOn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
        WireframeSectionCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Preferences", fontWeight = FontWeight.Bold, color = WireframeColors.Primary)
                Text("Daily scroll goal: ${if (demo) "45 min (demo)" else "Track to customize"}", color = WireframeColors.TextSecondary, fontSize = 14.sp)
                SettingsSwitchRow("Reminders & nudges", notifications) { vm.setNotifications(it) }
                SettingsSwitchRow("Demo data mode", demo) { vm.setDemoMode(it) }
            }
        }
        WireframeSectionCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Account & data", fontWeight = FontWeight.Bold, color = WireframeColors.Primary)
                SettingsLinkRow("Export summary") { }
                SettingsLinkRow("Privacy policy") { }
            }
        }
        WireframeSectionCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tracking", fontWeight = FontWeight.Bold, color = WireframeColors.Primary)
                Button(
                    onClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Usage access")
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = WireframeColors.TextPrimary)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SettingsLinkRow(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, color = WireframeColors.Primary)
    }
}
