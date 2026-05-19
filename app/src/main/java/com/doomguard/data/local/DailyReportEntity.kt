package com.doomguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey val dayEpochDay: Long,
    val doomScore: Int,
    val riskName: String,
    val socialMediaMinutes: Long,
    val longestSessionMinutes: Long,
    val nightUsage: Boolean,
    val mostUsedAppLabel: String,
    val aiText: String,
    val savedAt: Long,
)
