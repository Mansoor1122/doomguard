package com.doomguard.tracking

import com.doomguard.domain.UsageSummary

/**
 * Real usage breakdown from [UsageStatsReader.readUsageTimeSeries].
 */
data class UsageTimeSeries(
    val todaySummary: UsageSummary,
    val hourlyTodayMinutes: List<Int>,
    val dailyLast7DaysMinutes: List<Long>,
    val topAppsLast7Days: List<Pair<String, Int>>,
)
