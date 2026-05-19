package com.doomguard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.doomguard.BuildConfig
import com.doomguard.DoomGuardApp
import com.doomguard.ai.GeminiClient
import com.doomguard.ai.GeminiRepository
import com.doomguard.ai.WellbeingGraphicsPayload
import com.doomguard.data.local.DailyReportEntity
import com.doomguard.domain.AddictionAssessment
import com.doomguard.domain.UsageSummary
import com.doomguard.tracking.AddictionScorer
import com.doomguard.tracking.DemoUsageGenerator
import com.doomguard.tracking.DeviceUsageCollector
import com.doomguard.tracking.DeviceUsageSnapshot
import com.doomguard.tracking.UsageChartDerivation
import com.doomguard.tracking.UsageStatsReader
import com.doomguard.tracking.UsageTimeSeries
import com.doomguard.ui.wireframe.CoachFilterChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DoomViewModel(application: Application) : AndroidViewModel(application) {

    enum class UsagePeriod { Day, Week, Month }

    private val app = application as DoomGuardApp
    private val gemini = GeminiRepository(GeminiClient.create(BuildConfig.DEBUG))

    val demoMode = app.preferences.demoMode.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = false,
    )
    val notificationsOn = app.preferences.notificationsEnabled.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = true,
    )
    val onboardingComplete = app.preferences.onboardingComplete.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = false,
    )
    val displayName = app.preferences.displayName.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = "",
    )

    private val _usagePeriod = MutableStateFlow(UsagePeriod.Day)
    val usagePeriod: StateFlow<UsagePeriod> = _usagePeriod.asStateFlow()

    private val _coachFilter = MutableStateFlow(CoachFilterChip.All)
    val coachFilter: StateFlow<CoachFilterChip> = _coachFilter.asStateFlow()

    private val _hourly = MutableStateFlow(List(24) { 0f })
    val hourly: StateFlow<List<Float>> = _hourly.asStateFlow()

    private val _weekly = MutableStateFlow(List(7) { 0f })
    val weekly: StateFlow<List<Float>> = _weekly.asStateFlow()

    private val _topApps = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val topApps: StateFlow<List<Pair<String, Int>>> = _topApps.asStateFlow()

    private val _deviceSnapshot = MutableStateFlow<DeviceUsageSnapshot?>(null)
    val deviceSnapshot: StateFlow<DeviceUsageSnapshot?> = _deviceSnapshot.asStateFlow()

    private val _assessment = MutableStateFlow<AddictionAssessment?>(null)
    val assessment: StateFlow<AddictionAssessment?> = _assessment.asStateFlow()

    private val _aiText = MutableStateFlow<String?>(null)
    val aiText: StateFlow<String?> = _aiText.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _wellbeingGraphics = MutableStateFlow<WellbeingGraphicsPayload?>(null)
    val wellbeingGraphics: StateFlow<WellbeingGraphicsPayload?> = _wellbeingGraphics.asStateFlow()

    private val _graphicsLoading = MutableStateFlow(false)
    val graphicsLoading: StateFlow<Boolean> = _graphicsLoading.asStateFlow()

    private val _graphicsError = MutableStateFlow<String?>(null)
    val graphicsError: StateFlow<String?> = _graphicsError.asStateFlow()

    val history = app.database.dailyReportDao().observeReports()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun refreshUsage() {
        viewModelScope.launch {
            val demo = app.preferences.demoMode.first()
            computeAssessment(demo)
        }
    }

    private suspend fun computeAssessment(demo: Boolean) {
        val ctx = getApplication<Application>()
        _deviceSnapshot.value = if (demo) {
            DeviceUsageCollector.demoSnapshot()
        } else {
            DeviceUsageCollector.collect(ctx)
                ?: DeviceUsageSnapshot(0, emptyList(), 0, null)
        }
        if (demo) {
            val summary = DemoUsageGenerator.build()
            _assessment.value = AddictionScorer.assess(summary)
            applyChartsFromSummary(summary)
        } else {
            val series = UsageStatsReader.readUsageTimeSeries(ctx)
            if (series != null) {
                _assessment.value = AddictionScorer.assess(series.todaySummary)
                applyChartsFromSeries(series)
            } else {
                val empty = UsageSummary(0, 0, false, "—", 0, 0, 0)
                _assessment.value = AddictionScorer.assess(empty)
                applyChartsFromSummary(empty)
            }
        }
    }

    private fun applyChartsFromSummary(summary: UsageSummary) {
        val b = UsageChartDerivation.fromSummary(summary)
        _hourly.value = b.hourlyNormalized
        _weekly.value = b.weeklyNormalized
        _topApps.value = b.topApps
    }

    private fun applyChartsFromSeries(series: UsageTimeSeries) {
        val maxH = series.hourlyTodayMinutes.maxOrNull()?.coerceAtLeast(1) ?: 1
        _hourly.value = series.hourlyTodayMinutes.map { it.toFloat() / maxH }
        val maxD = series.dailyLast7DaysMinutes.maxOrNull()?.coerceAtLeast(1L) ?: 1L
        _weekly.value = series.dailyLast7DaysMinutes.map { day ->
            (day.toFloat() / maxD.toFloat()).coerceIn(0.05f, 1f)
        }
        _topApps.value = series.topAppsLast7Days
    }

    fun setUsagePeriod(p: UsagePeriod) {
        _usagePeriod.value = p
    }

    fun setCoachFilter(f: CoachFilterChip) {
        _coachFilter.value = f
    }

    fun completeOnboarding(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            app.preferences.setOnboardingComplete(true)
            onDone()
        }
    }

    fun setUserDisplayName(name: String, onSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            app.preferences.setDisplayName(name.trim())
            onSaved?.invoke()
        }
    }

    fun generateAiInsight() {
        val a = _assessment.value ?: return
        viewModelScope.launch {
            _aiLoading.value = true
            _aiError.value = null
            val result = gemini.generateInsight(a)
            result.onSuccess { text ->
                _aiText.value = text
                val day = LocalDate.now().toEpochDay()
                app.database.dailyReportDao().upsert(
                    DailyReportEntity(
                        dayEpochDay = day,
                        doomScore = a.score,
                        riskName = a.risk.name,
                        socialMediaMinutes = a.summary.totalSocialMediaMinutes,
                        longestSessionMinutes = a.summary.longestSessionMinutes,
                        nightUsage = a.summary.nightUsage,
                        mostUsedAppLabel = a.summary.mostUsedAppLabel,
                        aiText = text,
                        savedAt = System.currentTimeMillis(),
                    ),
                )
            }.onFailure { e ->
                _aiError.value = e.message ?: "Could not reach Gemini"
            }
            _aiLoading.value = false
        }
    }

    fun clearGraphicsError() {
        _graphicsError.value = null
    }

    fun generateVisualWellbeing() {
        val a = _assessment.value ?: return
        val d = _deviceSnapshot.value ?: return
        viewModelScope.launch {
            _graphicsLoading.value = true
            _graphicsError.value = null
            val result = gemini.generateWellbeingGraphics(a, d)
            result.onSuccess { _wellbeingGraphics.value = it }
                .onFailure { e -> _graphicsError.value = e.message ?: "Visual insight failed" }
            _graphicsLoading.value = false
        }
    }

    fun clearAiError() {
        _aiError.value = null
    }

    fun setDemoMode(v: Boolean) {
        viewModelScope.launch {
            app.preferences.setDemoMode(v)
            computeAssessment(v)
        }
    }

    fun setNotifications(v: Boolean) {
        viewModelScope.launch { app.preferences.setNotifications(v) }
    }
}
