package com.doomguard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReportDao {
    @Query("SELECT * FROM daily_reports ORDER BY dayEpochDay DESC LIMIT 30")
    fun observeReports(): Flow<List<DailyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyReportEntity)
}
