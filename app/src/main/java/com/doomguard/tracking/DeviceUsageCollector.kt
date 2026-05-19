package com.doomguard.tracking

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import java.util.Calendar

object DeviceUsageCollector {

    fun collect(context: Context): DeviceUsageSnapshot? {
        if (!UsageStatsReader.hasPermission(context)) return null
        val pm = context.packageManager
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val start = startOfTodayMillis()

        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, now)
            ?.asSequence()
            ?.filter { it.totalTimeInForeground > 0L }
            ?.sortedByDescending { it.totalTimeInForeground }
            ?.toList()
            .orEmpty()

        val top = stats.take(45).mapNotNull { st ->
            val label = appLabel(pm, st.packageName) ?: return@mapNotNull null
            AppUsageEntry(
                displayName = label,
                minutesToday = (st.totalTimeInForeground / 60_000L).toInt().coerceAtLeast(0),
                packageName = st.packageName,
            )
        }.filter { it.minutesToday > 0 }

        val totalMin = (stats.sumOf { it.totalTimeInForeground } / 60_000L).toInt().coerceAtLeast(0)
        val installedUser = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .count { isUserFacingApp(it) }
        } catch (_: Exception) {
            null
        }

        return DeviceUsageSnapshot(
            totalScreenMinutesToday = totalMin,
            topApps = top,
            distinctPackagesUsedToday = stats.size,
            approximateInstalledUserApps = installedUser,
        )
    }

    fun demoSnapshot(): DeviceUsageSnapshot {
        // Fictional labels; package names only classify “social” for scoring — avoids implying apps are installed.
        val apps = listOf(
            AppUsageEntry("Feed scroll", 72, "com.instagram.android"),
            AppUsageEntry("Video hub", 48, "com.google.android.youtube"),
            AppUsageEntry("Browser", 35, "com.android.chrome"),
            AppUsageEntry("Messages", 22, "com.whatsapp"),
            AppUsageEntry("Clip loop", 18, "com.zhiliaoapp.musically"),
            AppUsageEntry("Mail", 12, "com.google.android.gm"),
            AppUsageEntry("Settings", 4, "com.android.settings"),
        )
        return DeviceUsageSnapshot(
            totalScreenMinutesToday = apps.sumOf { it.minutesToday },
            topApps = apps,
            distinctPackagesUsedToday = apps.size,
            approximateInstalledUserApps = 86,
        )
    }

    private fun startOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun appLabel(pm: PackageManager, packageName: String): String? = try {
        val ai = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(ai).toString()
    } catch (_: Exception) {
        null
    }

    private fun isUserFacingApp(info: ApplicationInfo): Boolean {
        val isSystem = info.flags and ApplicationInfo.FLAG_SYSTEM != 0
        val isUpdatedSystem = info.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
        when {
            isUpdatedSystem -> return true
            !isSystem -> return true
            else -> return false
        }
    }
}
