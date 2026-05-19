package com.doomguard.tracking

import com.doomguard.domain.AddictionAssessment
import com.doomguard.domain.RiskLevel
import com.doomguard.domain.UsageSummary
import kotlin.math.min
import kotlin.math.roundToInt

object AddictionScorer {
    fun assess(summary: UsageSummary): AddictionAssessment {
        val continuousWeight = min(40, (summary.continuousSocialMinutes * 40 / 120).toInt())
        val lateNightWeight = if (summary.nightUsage) 25 else 0
        val socialWeight = min(30, (summary.totalSocialMediaMinutes * 30 / 180).toInt())
        val frequencyWeight = min(15, summary.appOpenCount / 3)
        val raw = continuousWeight + lateNightWeight + socialWeight + frequencyWeight
        val score = raw.coerceIn(0, 100)
        return AddictionAssessment(score = score, risk = RiskLevel.fromScore(score), summary = summary)
    }
}
