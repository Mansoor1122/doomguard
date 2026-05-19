package com.doomguard

import android.app.Application
import com.doomguard.data.local.DoomDatabase
import com.doomguard.data.prefs.DoomPreferencesRepository
import com.doomguard.notifications.WellnessNotifier

class DoomGuardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WellnessNotifier.ensureChannel(this)
    }

    val database by lazy { DoomDatabase.build(this) }
    val preferences by lazy { DoomPreferencesRepository(this) }
}
