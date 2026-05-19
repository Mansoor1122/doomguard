package com.doomguard.tracking

import com.doomguard.domain.UsageSummary
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Derives chart data only from [UsageSummary] so demo mode is never tied to fixed app names.
 */
object UsageChartDerivation {

    data class ChartBundle(
        val hourlyNormalized: List<Float>,
        val weeklyNormalized: List<Float>,
        val topApps: List<Pair<String, Int>>,
    )

    fun fromSummary(s: UsageSummary): ChartBundle {
        if (s.totalSocialMediaMinutes <= 0L) {
            return ChartBundle(
                hourlyNormalized = List(24) { 0.05f },
                weeklyNormalized = List(7) { 0.05f },
                topApps = emptyList(),
            )
        }
        val totalM = s.totalSocialMediaMinutes.toFloat()
        val hourlyWeights = FloatArray(24) { h ->
            val evening = exp(-((h - 21) * (h - 21)) / 20f)
            val lunch = exp(-((h - 13) * (h - 13)) / 28f)
            val night = if (s.nightUsage && (h <= 5 || h >= 23)) 0.5f else 0f
            0.15f + 0.95f * evening + 0.45f * lunch + night
        }
        val wSum = hourlyWeights.sum().coerceAtLeast(0.01f)
        val hourlyMinutes = hourlyWeights.map { (it / wSum * totalM).toInt() }
        val maxH = hourlyMinutes.maxOrNull()?.coerceAtLeast(1) ?: 1
        val hourlyNorm = hourlyMinutes.map { it.toFloat() / maxH }

        val baseDaily = totalM / 7f
        val daily = List(7) { i ->
            val wave = (0.75f + 0.25f * sin(i * PI / 3.5).toFloat()).coerceIn(0.35f, 1.25f)
            (baseDaily * wave).toLong().coerceAtLeast(0L)
        }
        val maxD = daily.maxOrNull()?.coerceAtLeast(1L) ?: 1L
        val weekNorm = daily.map { (it.toFloat() / maxD).coerceIn(0.05f, 1f) }

        val top = buildDemoTopApps(s, totalM.toLong())

        return ChartBundle(hourlyNorm, weekNorm, top)
    }

    private fun buildDemoTopApps(s: UsageSummary, totalM: Long): List<Pair<String, Int>> {
        if (totalM <= 0L) return emptyList()
        val primary = s.mostUsedAppLabel.ifBlank { "Social" }
        val primaryMin = (totalM * 0.55).toInt().coerceAtLeast(1)
        val rest = (totalM - primaryMin).coerceAtLeast(0).toInt()
        return buildList {
            add(primary to primaryMin)
            if (rest > 0) add("Other social (estimate)" to rest)
        }
    }
}
