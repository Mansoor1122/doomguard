package com.doomguard.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("doom_prefs")

class DoomPreferencesRepository(private val context: Context) {

    private object Keys {
        val demoMode = booleanPreferencesKey("demo_mode")
        val notifications = booleanPreferencesKey("notifications")
        val aiTone = stringPreferencesKey("ai_tone")
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val displayName = stringPreferencesKey("display_name")
    }

    /** Off by default so Home / Insights reflect the real device once usage access is granted. */
    val demoMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.demoMode] ?: false }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.notifications] ?: true }
    val aiTone: Flow<String> = context.dataStore.data.map { it[Keys.aiTone] ?: "friendly" }
    val onboardingComplete: Flow<Boolean> = context.dataStore.data.map { it[Keys.onboardingComplete] ?: false }
    val displayName: Flow<String> = context.dataStore.data.map { it[Keys.displayName] ?: "" }

    suspend fun setDemoMode(v: Boolean) {
        context.dataStore.edit { it[Keys.demoMode] = v }
    }

    suspend fun setNotifications(v: Boolean) {
        context.dataStore.edit { it[Keys.notifications] = v }
    }

    suspend fun setAiTone(tone: String) {
        context.dataStore.edit { it[Keys.aiTone] = tone }
    }

    suspend fun setOnboardingComplete(v: Boolean) {
        context.dataStore.edit { it[Keys.onboardingComplete] = v }
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[Keys.displayName] = name }
    }
}
