package com.doomguard.ai

import com.squareup.moshi.Json

data class WellbeingGraphicsPayload(
    @Json(name = "headline") val headline: String = "",
    @Json(name = "emoji") val emoji: String? = null,
    @Json(name = "wellness_score") val wellnessScore: Int = 0,
    @Json(name = "energy") val energy: Int = 0,
    @Json(name = "calm") val calm: Int = 0,
    @Json(name = "focus") val focus: Int = 0,
    @Json(name = "time_balance") val timeBalance: List<TimeBalanceSlice> = emptyList(),
    @Json(name = "app_heavyweights") val appHeavyweights: List<AppHeavyweight> = emptyList(),
    @Json(name = "spark_words") val sparkWords: List<String> = emptyList(),
)

data class TimeBalanceSlice(
    @Json(name = "label") val label: String = "",
    @Json(name = "percent") val percent: Int = 0,
    @Json(name = "tone") val tone: String = "neutral",
)

data class AppHeavyweight(
    @Json(name = "app") val app: String = "",
    @Json(name = "minutes") val minutes: Int = 0,
    @Json(name = "heat") val heat: Int = 1,
)
