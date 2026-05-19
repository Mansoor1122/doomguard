package com.doomguard.tracking

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.doomguard.domain.UsageSummary
import java.util.Calendar
import java.util.concurrent.TimeUnit

object UsageStatsReader {

    private const val DAY_MS = 24 * 60 * 60 * 1000L

    fun hasPermission(context: Context): Boolean {
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.HOURS.toMillis(1)
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
        return stats != null && stats.isNotEmpty()
    }

    fun readToday(context: Context): UsageSummary? =
        readUsageTimeSeries(context)?.todaySummary

    /**
     * Social-app usage for today plus last 6 days (7 calendar days), with hourly split for today.
     */
    fun readUsageTimeSeries(context: Context): UsageTimeSeries? {
        if (!hasPermission(context)) return null
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val startToday = startOfTodayMillis()
        val startWeek = startToday - 6 * DAY_MS
        val sessions = parseSocialSessions(usm, startWeek, now)

        val todaySessions = sessions.filter { (it.end ?: now) > startToday && it.start < now }
        val todaySummary = buildSummaryFromSessions(todaySessions, startToday, now)

        val hourly = IntArray(24)
        for (s in todaySessions) {
            val overlapStart = maxOf(s.start, startToday)
            val overlapEnd = minOf(s.end ?: now, now)
            if (overlapEnd <= overlapStart) continue
            addMinutesToHourlyBins(hourly, overlapStart, overlapEnd)
        }

        val daily = LongArray(7)
        for (d in 0 until 7) {
            val dayStart = startWeek + d * DAY_MS
            val dayEnd = dayStart + DAY_MS
            daily[d] = overlappingSocialMinutes(sessions, dayStart, minOf(dayEnd, now), now)
        }

        val perPkgMs = mutableMapOf<String, Long>()
        for (s in sessions) {
            val dur = (minOf(s.end ?: now, now) - s.start).coerceAtLeast(0L)
            perPkgMs[s.pkg] = perPkgMs.getOrDefault(s.pkg, 0L) + dur
        }
        val topApps = perPkgMs.entries
            .sortedByDescending { it.value }
            .take(8)
            .map { (pkg, ms) ->
                val label = SocialPackages.labels[pkg] ?: prettifyPkg(pkg)
                label to (ms / 60_000).toInt()
            }

        return UsageTimeSeries(
            todaySummary = todaySummary,
            hourlyTodayMinutes = hourly.toList(),
            dailyLast7DaysMinutes = daily.toList(),
            topAppsLast7Days = topApps,
        )
    }

    private fun overlappingSocialMinutes(
        sessions: List<Session>,
        rangeStart: Long,
        rangeEnd: Long,
        now: Long,
    ): Long {
        var ms = 0L
        for (s in sessions) {
            val overlapStart = maxOf(s.start, rangeStart)
            val overlapEnd = minOf(s.end ?: now, rangeEnd)
            if (overlapEnd > overlapStart) ms += overlapEnd - overlapStart
        }
        return ms / 60_000
    }

    private fun addMinutesToHourlyBins(hourly: IntArray, overlapStart: Long, overlapEnd: Long) {
        var t = overlapStart
        while (t < overlapEnd) {
            val cal = Calendar.getInstance().apply { timeInMillis = t }
            val h = cal.get(Calendar.HOUR_OF_DAY)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val hourStart = cal.timeInMillis
            val nextHour = hourStart + TimeUnit.HOURS.toMillis(1)
            val segEnd = minOf(nextHour, overlapEnd)
            hourly[h] += ((segEnd - t) / 60_000).toInt().coerceAtLeast(0)
            t = segEnd
        }
    }

    private fun parseSocialSessions(usm: UsageStatsManager, start: Long, end: Long): List<Session> {
        val events = usm.queryEvents(start, end)
        val event = UsageEvents.Event()
        val sessions = mutableListOf<Session>()
        var current: Session? = null
        val now = System.currentTimeMillis()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName ?: continue
            if (!SocialPackages.isSocial(pkg)) continue
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    current = Session(pkg, event.timeStamp, null)
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    val c = current
                    if (c != null && c.pkg == pkg) {
                        sessions += c.copy(end = event.timeStamp)
                        current = null
                    }
                }
            }
        }
        current?.let { sessions += it.copy(end = minOf(now, end)) }
        return sessions
    }

    private fun buildSummaryFromSessions(sessions: List<Session>, @Suppress("UNUSED_PARAMETER") startToday: Long, now: Long): UsageSummary {
        if (sessions.isEmpty()) {
            return UsageSummary(
                totalSocialMediaMinutes = 0,
                longestSessionMinutes = 0,
                nightUsage = false,
                mostUsedAppLabel = "—",
                appOpenCount = 0,
                estimatedDoomscrollMinutes = 0,
                continuousSocialMinutes = 0,
            )
        }
        val perPkg = sessions.groupBy { it.pkg }.mapValues { (_, list) ->
            list.sumOf { s -> s.durationMs(now).coerceAtLeast(0L) }
        }
        val totalMs = perPkg.values.sum()
        val longestMs = sessions.maxOf { it.durationMs(now).coerceAtLeast(0L) }
        val mostUsed = perPkg.maxByOrNull { it.value }?.key.orEmpty()
        val label = SocialPackages.labels[mostUsed] ?: prettifyPkg(mostUsed)
        val foregroundOpens = sessions.size

        var continuousMax = 0L
        val sorted = sessions.sortedBy { it.start }
        var runStart = sorted.first().start
        var runPkg = sorted.first().pkg
        var runEnd = sorted.first().end ?: now
        for (i in 1 until sorted.size) {
            val s = sorted[i]
            val gap = s.start - runEnd
            if (s.pkg == runPkg && gap < 2 * 60_000) {
                runEnd = s.end ?: now
            } else {
                if (SocialPackages.isSocial(runPkg)) {
                    continuousMax = maxOf(continuousMax, runEnd - runStart)
                }
                runStart = s.start
                runPkg = s.pkg
                runEnd = s.end ?: now
            }
        }
        if (SocialPackages.isSocial(runPkg)) {
            continuousMax = maxOf(continuousMax, runEnd - runStart)
        }

        val night = sessions.any { nightSpan(it.start) || nightSpan(it.end ?: now) }
        val doomEstimate = (totalMs * 0.75).toLong().coerceAtMost(totalMs)

        return UsageSummary(
            totalSocialMediaMinutes = totalMs / 60_000,
            longestSessionMinutes = longestMs / 60_000,
            nightUsage = night,
            mostUsedAppLabel = label,
            appOpenCount = foregroundOpens,
            estimatedDoomscrollMinutes = doomEstimate / 60_000,
            continuousSocialMinutes = continuousMax / 60_000,
        )
    }

    private fun nightSpan(ts: Long): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = ts }
        val h = cal.get(Calendar.HOUR_OF_DAY)
        return h in 0..5 || h >= 23
    }

    private fun startOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun prettifyPkg(pkg: String): String =
        pkg.substringAfterLast('.').replaceFirstChar { it.titlecase() }

    private data class Session(val pkg: String, val start: Long, val end: Long?) {
        fun durationMs(now: Long): Long = (end ?: now) - start
    }
}
