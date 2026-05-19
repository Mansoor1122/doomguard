package com.doomguard.domain

enum class RiskLevel {
    Healthy,
    Moderate,
    High,
    Severe;

    companion object {
        fun fromScore(score: Int): RiskLevel = when {
            score <= 30 -> Healthy
            score <= 60 -> Moderate
            score <= 80 -> High
            else -> Severe
        }
    }
}

data class UsageSummary(
    val totalSocialMediaMinutes: Long,
    val longestSessionMinutes: Long,
    val nightUsage: Boolean,
    val mostUsedAppLabel: String,
    val appOpenCount: Int,
    val estimatedDoomscrollMinutes: Long,
    val continuousSocialMinutes: Long,
)

data class AddictionAssessment(
    val score: Int,
    val risk: RiskLevel,
    val summary: UsageSummary,
)
