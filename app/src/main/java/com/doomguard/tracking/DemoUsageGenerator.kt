package com.doomguard.tracking

import com.doomguard.domain.UsageSummary
import kotlin.math.min

object DemoUsageGenerator {

    /** Matches [DeviceUsageCollector.demoSnapshot] so UI never shows hardcoded brands or mismatched totals. */
    fun build(): UsageSummary {
        val snap = DeviceUsageCollector.demoSnapshot()
        val socialApps = snap.topApps.filter { SocialPackages.isSocial(it.packageName) }
        val totalSocial = socialApps.sumOf { it.minutesToday.toLong() }
        val topSocial = socialApps.maxByOrNull { it.minutesToday }
        val mostUsed = topSocial?.displayName ?: snap.topApps.firstOrNull()?.displayName ?: "—"
        val longestSingle = socialApps.maxOfOrNull { it.minutesToday }?.toLong() ?: 0L
        val longestSession = maxOf(longestSingle, totalSocial * 35L / 100L).coerceAtLeast(0)
        val continuous = min(90L, maxOf(longestSession, totalSocial * 40L / 100L))
        val opens = (10 + snap.topApps.size * 5).coerceIn(8, 48)

        return UsageSummary(
            totalSocialMediaMinutes = totalSocial,
            longestSessionMinutes = longestSession,
            nightUsage = totalSocial >= 75,
            mostUsedAppLabel = mostUsed,
            appOpenCount = opens,
            estimatedDoomscrollMinutes = (totalSocial * 7 / 10).coerceAtLeast(0),
            continuousSocialMinutes = continuous,
        )
    }
}
