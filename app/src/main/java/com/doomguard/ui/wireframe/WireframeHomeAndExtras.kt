package com.doomguard.ui.wireframe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doomguard.domain.AddictionAssessment
import com.doomguard.domain.RiskLevel
import com.doomguard.tracking.DeviceUsageSnapshot
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.doomCyberPrimaryFill
import com.doomguard.ui.theme.DoomGuardTheme
import com.doomguard.ui.theme.DoomThemeVariant
import com.doomguard.ui.theme.WireframeColors
import kotlinx.coroutines.delay

@Composable
fun HomeDashboardTab(
    displayName: String,
    assessment: AddictionAssessment?,
    demo: Boolean,
    hasUsagePermission: Boolean,
    deviceSnapshot: DeviceUsageSnapshot?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val greeting = if (displayName.isBlank()) "Hi there" else "Hi, $displayName"
        Text(
            greeting,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = WireframeColors.TextPrimary,
        )
        Text(
            when {
                demo -> "Showing demo usage • impressive pitch mode"
                !hasUsagePermission -> "Enable usage access in Settings for live stats"
                else -> "Here’s how your scrolling looks today"
            },
            color = WireframeColors.TextSecondary,
            fontSize = 14.sp,
        )
        if (assessment == null) {
            Spacer(Modifier.height(32.dp))
            return@Column
        }
        val a = assessment
        val snap = deviceSnapshot
        val totalScreen = snap?.totalScreenMinutesToday ?: 0
        val screenLabel = when {
            demo -> "${totalScreen}m"
            !hasUsagePermission -> "—"
            totalScreen > 0 -> "${totalScreen}m"
            else -> "0m"
        }
        val mostUsedLabel = when {
            snap?.topApps?.isNotEmpty() == true -> snap.topApps.first().displayName
            a.summary.mostUsedAppLabel.isNotBlank() && a.summary.mostUsedAppLabel != "—" -> a.summary.mostUsedAppLabel
            else -> "—"
        }
        WireframeSectionCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Doomscrolling score", fontWeight = FontWeight.SemiBold, color = WireframeColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                CircularDoomScore(a.score, a.risk)
                Spacer(Modifier.height(8.dp))
                Text(
                    wireframeRiskTitle(a.risk),
                    color = riskAccentColor(a.risk),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
        Text("Today’s overview", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
        Text(
            when {
                demo -> "Sample device totals — toggle off demo in Settings for live data"
                !hasUsagePermission -> "Grant usage access to see screen time and top apps"
                else -> "Total screen time from usage stats; social stats use tracked social apps only"
            },
            color = WireframeColors.TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TodayMiniCard(
                title = "Total screen",
                value = screenLabel,
                modifier = Modifier.weight(1f),
            )
            TodayMiniCard(
                title = "Longest social session",
                value = "${a.summary.longestSessionMinutes}m",
                modifier = Modifier.weight(1f),
            )
        }
        TodayMiniCard(
            title = "Most used (today)",
            value = mostUsedLabel,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TodayMiniCard(title: String, value: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    WireframeSectionCard(modifier = modifier) {
        Column {
            Text(title, fontSize = 13.sp, color = WireframeColors.TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun wireframeRiskTitle(r: RiskLevel): String = when (r) {
    RiskLevel.Healthy -> "Healthy"
    RiskLevel.Moderate -> "Moderate risk"
    RiskLevel.High -> "High risk"
    RiskLevel.Severe -> "Severe risk"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(DoomBrushes.hullVertical),
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Achievements", color = WireframeColors.TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WireframeColors.CyanBright,
                    )
                }
            },
            actions = {
                Icon(Icons.Default.EmojiEvents, null, tint = WireframeColors.Warning)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                titleContentColor = WireframeColors.TextPrimary,
                navigationIconContentColor = WireframeColors.CyanBright,
                actionIconContentColor = WireframeColors.Warning,
            ),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AchievementTile("First step", "Complete onboarding", 1f)
            AchievementTile("Insight seeker", "Generate a Gemini coaching note", 0.6f)
            AchievementTile("Focus achiever", "Finish a focus session", 0f)
            AchievementTile("Break master", "Act on a scrolling nudge", 0.35f)
        }
    }
}

@Composable
fun FocusModeScreen(
    onEnd: () -> Unit,
) {
    val sessionMinutes = 25
    val totalSec = sessionMinutes * 60
    var remainingSec by remember { mutableIntStateOf(totalSec) }
    var paused by remember { mutableStateOf(false) }
    val finished = remainingSec <= 0

    LaunchedEffect(paused) {
        while (!paused && remainingSec > 0) {
            delay(1000)
            remainingSec -= 1
        }
    }

    val mm = (remainingSec.coerceAtLeast(0)) / 60
    val ss = (remainingSec.coerceAtLeast(0)) % 60
    val timeText = "%d:%02d".format(mm, ss)

    val progress = remainingSec.coerceAtLeast(0).toFloat() / totalSec.toFloat()
    val sweepDegrees = progress * 360f

    DoomGuardTheme(variant = DoomThemeVariant.DarkFocus) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DoomBrushes.hullVertical)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Focus mode",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WireframeColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(28.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(280.dp),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                        val r = size.minDimension / 2
                        drawArc(
                            color = WireframeColors.SurfaceElevated.copy(alpha = 0.95f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Round),
                        )
                        drawArc(
                            brush = DoomBrushes.primaryHorizontal,
                            startAngle = -90f,
                            sweepAngle = sweepDegrees,
                            useCenter = false,
                            style = stroke,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            timeText,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (finished) WireframeColors.Warning else WireframeColors.CyanBright,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (paused) "Paused" else "${(progress * 100).toInt()}% remaining",
                            color = WireframeColors.TextSecondary,
                            fontSize = 15.sp,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    when {
                        finished -> "Session complete — great work."
                        paused -> "Resume when you're ready."
                        else -> "$sessionMinutes-minute focus session — cyan ring tracks your runway."
                    },
                    color = WireframeColors.TextSecondary,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(28.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(
                        onClick = { paused = !paused },
                        enabled = !finished,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(WireframeColors.CyanGlow.copy(alpha = 0.7f), WireframeColors.PurpleGlow.copy(alpha = 0.55f)),
                            ),
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = WireframeColors.SurfaceElevated.copy(alpha = 0.72f),
                            contentColor = WireframeColors.TextPrimary,
                        ),
                    ) {
                        Text(if (paused) "Resume" else "Pause", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onEnd,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = WireframeColors.TextPrimary,
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().doomCyberPrimaryFill(enabled = true),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("End session", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NudgeFullscreen(
    minutes: Int,
    onTakeBreak: () -> Unit,
    onContinue: () -> Unit,
) {
    DoomGuardTheme(variant = DoomThemeVariant.DarkFocus) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DoomBrushes.hullVertical)
                .padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "You’ve been scrolling for $minutes minutes",
                style = MaterialTheme.typography.headlineSmall,
                color = WireframeColors.TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Your eyes and focus will thank you for a short pause.",
                color = WireframeColors.TextSecondary,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onTakeBreak,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WireframeColors.Success, contentColor = Color(0xFF050816)),
            ) {
                Text("Take a 5-min break", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(WireframeColors.CyanGlow.copy(alpha = 0.8f), WireframeColors.Warning.copy(alpha = 0.7f)),
                    ),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = WireframeColors.SurfaceElevated.copy(alpha = 0.82f),
                    contentColor = WireframeColors.TextPrimary,
                ),
            ) {
                Text("Continue anyway", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
