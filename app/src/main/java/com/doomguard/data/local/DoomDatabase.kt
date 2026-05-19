package com.doomguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyReportEntity::class], version = 1, exportSchema = false)
abstract class DoomDatabase : RoomDatabase() {
    abstract fun dailyReportDao(): DailyReportDao

    companion object {
        fun build(context: Context): DoomDatabase =
            Room.databaseBuilder(context, DoomDatabase::class.java, "doomguard.db").build()
    }
}
