package com.doomguard.ui.wireframe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doomguard.ai.AppHeavyweight
import com.doomguard.ai.TimeBalanceSlice
import com.doomguard.ai.WellbeingGraphicsPayload
import com.doomguard.ui.theme.WireframeColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WellbeingGraphicsPanel(payload: WellbeingGraphicsPayload) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = payload.emoji?.ifBlank { "🌿" } ?: "🌿",
                fontSize = 42.sp,
            )
            Column {
                Text(
                    text = payload.headline.ifBlank { "Your digital pulse" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WireframeColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Wellness ${payload.wellnessScore.coerceIn(0, 100)}",
                    fontWeight = FontWeight.SemiBold,
                    color = WireframeColors.Primary,
                    fontSize = 18.sp,
                )
            }
        }

        ThreeMeters(
            energy = payload.energy.coerceIn(0, 100),
            calm = payload.calm.coerceIn(0, 100),
            focus = payload.focus.coerceIn(0, 100),
        )

        if (payload.timeBalance.isNotEmpty()) {
            Text("Time mix (AI)", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary, fontSize = 14.sp)
            StackedToneBar(payload.timeBalance)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                payload.timeBalance.forEach { s ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(
                            s.label,
                            fontSize = 11.sp,
                            color = WireframeColors.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text("${s.percent}%", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = toneColor(s.tone))
                    }
                }
            }
        }

        if (payload.appHeavyweights.isNotEmpty()) {
            Text("Focus apps", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary, fontSize = 14.sp)
            payload.appHeavyweights.forEach { h ->
                HeavyweightRow(h)
            }
        }

        if (payload.sparkWords.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                payload.sparkWords.forEach { w ->
                    AssistChip(
                        onClick = { },
                        label = { Text(w, maxLines = 1) },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = WireframeColors.CyanBright,
                            containerColor = WireframeColors.SurfaceLight.copy(alpha = 0.45f),
                            leadingIconContentColor = WireframeColors.CyanGlow,
                        ),
                        border = BorderStroke(1.dp, WireframeColors.CyanGlow.copy(alpha = 0.45f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThreeMeters(energy: Int, calm: Int, focus: Int) {
    @Composable
    fun meter(label: String, value: Int, color: Color) {
        Column(Modifier.padding(bottom = 6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(label, fontSize = 13.sp, color = WireframeColors.TextSecondary)
                Text("$value", fontWeight = FontWeight.Bold, color = WireframeColors.TextPrimary)
            }
            LinearProgressIndicator(
                progress = { value / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = color,
                trackColor = WireframeColors.Neutral.copy(alpha = 0.15f),
            )
        }
    }
    meter("Energy", energy, WireframeColors.Warning)
    meter("Calm", calm, WireframeColors.Primary)
    meter("Focus", focus, WireframeColors.Success)
}

@Composable
private fun StackedToneBar(slices: List<TimeBalanceSlice>) {
    val total = slices.sumOf { it.percent }.coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .clip(RoundedCornerShape(11.dp)),
    ) {
        slices.forEach { s ->
            val w = maxOf(s.percent, 1) / total.toFloat()
            Box(
                modifier = Modifier
                    .weight(w)
                    .fillMaxHeight()
                    .background(toneColor(s.tone)),
            )
        }
    }
}

@Composable
private fun HeavyweightRow(h: AppHeavyweight) {
    val heatColor = when (h.heat.coerceIn(1, 3)) {
        3 -> WireframeColors.Danger
        2 -> WireframeColors.Warning
        else -> WireframeColors.Neutral
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(heatColor),
        )
        Text(
            h.app,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            color = WireframeColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text("${h.minutes}m", color = WireframeColors.TextSecondary, fontSize = 14.sp)
    }
}

private fun toneColor(tone: String): Color = when (tone.lowercase()) {
    "good" -> WireframeColors.Success
    "warn" -> WireframeColors.Warning
    "alert" -> WireframeColors.Danger
    else -> WireframeColors.Neutral.copy(alpha = 0.5f)
}
