package com.doomguard.tracking

/**
 * Full-device foreground usage (today) plus install context for AI + UI.
 */
data class DeviceUsageSnapshot(
    val totalScreenMinutesToday: Int,
    /** Launcher-resolved labels, highest usage first */
    val topApps: List<AppUsageEntry>,
    val distinctPackagesUsedToday: Int,
    /** User apps visible to PackageManager (may be partial without QUERY_ALL_PACKAGES). */
    val approximateInstalledUserApps: Int?,
)

data class AppUsageEntry(
    val displayName: String,
    val minutesToday: Int,
    val packageName: String,
)
