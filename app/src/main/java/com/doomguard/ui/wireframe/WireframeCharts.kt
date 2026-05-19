package com.doomguard.ui.wireframe

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doomguard.domain.RiskLevel
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.DoomGlassCard
import com.doomguard.ui.theme.WireframeColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WireframeSectionCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    content: @Composable () -> Unit,
) {
    DoomGlassCard(modifier = modifier, contentPadding = PaddingValues(20.dp), elevation = 10.dp, content = content)
}

fun riskAccentColor(r: RiskLevel): Color = when (r) {
    RiskLevel.Healthy -> WireframeColors.Success
    RiskLevel.Moderate -> WireframeColors.Warning
    RiskLevel.High, RiskLevel.Severe -> WireframeColors.Danger
}

@Composable
fun CircularDoomScore(score: Int, risk: RiskLevel, modifier: Modifier = Modifier) {
    val accent = riskAccentColor(risk)
    val infinite = rememberInfiniteTransition(label = "doom_pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1800), repeatMode = RepeatMode.Reverse),
        label = "pulse",
    )
    val haloAlpha = (0.12f + 0.08f * pulse).coerceIn(0f, 1f)
    Box(modifier.then(Modifier.size(172.dp)), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            strokeWidth = 16.dp,
            color = accent.copy(alpha = haloAlpha),
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
        )
        CircularProgressIndicator(
            progress = { (score / 100f).coerceIn(0.05f, 1f) },
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            strokeWidth = 12.dp,
            color = accent,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            strokeCap = StrokeCap.Round,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$score",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = WireframeColors.TextPrimary,
            )
            Text(
                "/100",
                color = WireframeColors.TextSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun DayHourUsageChart(values: List<Float>, modifier: Modifier = Modifier) {
    val raw = values.take(24).ifEmpty { List(24) { 0.05f } }
    val bars = raw + List((24 - raw.size).coerceAtLeast(0)) { 0.05f }
    val labelHours = listOf(0, 3, 6, 9, 12, 15, 18, 21)
    val labels = labelHours.map { formatHour12h(it) }
    Column(modifier) {
        Row(
            Modifier
                .height(140.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            bars.take(24).forEach { v ->
                val frac = v.coerceIn(0f, 1f)
                val barH = (8 + frac * 118).dp
                Box(
                    Modifier
                        .weight(1f)
                        .height(barH)
                        .background(
                            DoomBrushes.chartBar,
                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                        ),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Box(
                    Modifier.weight(3f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        fontSize = 9.sp,
                        maxLines = 1,
                        color = WireframeColors.TextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun WeekTrendLineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
) {
    val dayLabels = rememberLast7DayShortLabels()
    Column(modifier) {
        WeeklyLineChart(points)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = WireframeColors.TextSecondary,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun MonthWeekBarsChart(values: List<Float>, modifier: Modifier = Modifier) {
    val raw = values.take(4).ifEmpty { List(4) { 0.05f } }
    val bars = raw + List((4 - raw.size).coerceAtLeast(0)) { 0.05f }
    val labels = listOf("Week 1", "Week 2", "Week 3", "Week 4")
    Column(modifier) {
        Row(
            Modifier
                .height(140.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            bars.take(4).forEach { v ->
                val frac = v.coerceIn(0f, 1f)
                val barH = (8 + frac * 118).dp
                Box(
                    Modifier
                        .weight(1f)
                        .height(barH)
                        .background(
                            DoomBrushes.chartBar,
                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        ),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = WireframeColors.TextSecondary,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberLast7DayShortLabels(): List<String> {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    return remember(zone, today) {
        (0 until 7).map { offset ->
            val d = today.minusDays(6 - offset.toLong())
            d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }
}

private fun formatHour12h(hour0To23: Int): String {
    val t = LocalTime.of(hour0To23.coerceIn(0, 23), 0)
    val fmt = DateTimeFormatter.ofPattern("h a", Locale.getDefault())
    return t.format(fmt)
}

@Composable
fun UsageBarChart(values: List<Float>, modifier: Modifier = Modifier) {
    val capped = values.take(24).ifEmpty { List(24) { 0.1f } }
    Row(
        modifier
            .height(140.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        capped.forEach { v ->
            val frac = v.coerceIn(0f, 1f)
            val barH = (8 + frac * 118).dp
            Box(
                Modifier
                    .weight(1f)
                    .height(barH)
                    .background(
                        DoomBrushes.chartBar,
                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                    ),
            )
        }
    }
}

@Composable
fun WeeklyLineChart(points: List<Float>, modifier: Modifier = Modifier) {
    val p = if (points.size >= 2) points.take(7) else List(7) { 0.4f }
    Canvas(modifier = modifier.height(160.dp).fillMaxWidth()) {
        val w = size.width
        val h = size.height
        val n = p.size
        val stepX = if (n <= 1) 0f else w / (n - 1)
        val pts = p.mapIndexed { i, v ->
            val x = i * stepX
            val y = h - v.coerceIn(0.05f, 1f) * h * 0.85f - 8f
            Offset(x, y)
        }
        for (i in 0 until pts.lastIndex) {
            drawLine(
                color = WireframeColors.PurpleNeon.copy(alpha = 0.35f),
                start = pts[i],
                end = pts[i + 1],
                strokeWidth = 10f,
                cap = StrokeCap.Round,
            )
        }
        for (i in 0 until pts.lastIndex) {
            val brushSeg = Brush.linearGradient(
                colors = listOf(WireframeColors.CyanGlow, WireframeColors.PurpleNeon),
                start = pts[i],
                end = pts[i + 1],
            )
            drawLine(
                brush = brushSeg,
                start = pts[i],
                end = pts[i + 1],
                strokeWidth = 5f,
                cap = StrokeCap.Round,
            )
        }
        pts.forEach { pt ->
            drawCircle(color = WireframeColors.BackgroundLight, radius = 10f, center = pt)
            drawCircle(color = WireframeColors.CyanBright, radius = 7f, center = pt)
        }
    }
}

@Composable
fun TopAppsList(apps: List<Pair<String, Int>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        apps.forEach { (name, minutes) ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name, fontWeight = FontWeight.Medium, color = WireframeColors.TextPrimary)
                Text("$minutes min", color = WireframeColors.TextSecondary)
            }
            HorizontalDivider(color = WireframeColors.Neutral.copy(alpha = 0.22f))
        }
    }
}

@Composable
fun AchievementTile(title: String, subtitle: String, progress: Float) {
    DoomGlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 6.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
        Text(subtitle, color = WireframeColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = WireframeColors.CyanBright,
            trackColor = WireframeColors.SurfaceElevated.copy(alpha = 0.8f),
        )
    }
}
