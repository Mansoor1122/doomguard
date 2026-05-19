package com.doomguard.ai

import com.doomguard.domain.AddictionAssessment
import com.doomguard.tracking.DeviceUsageSnapshot
import org.json.JSONArray
import org.json.JSONObject

object PromptBuilder {
    fun wellnessCoachPrompt(assessment: AddictionAssessment): String {
        val s = assessment.summary
        val hours = s.totalSocialMediaMinutes / 60
        val mins = s.totalSocialMediaMinutes % 60
        val totalStr = "${hours}h ${mins}m"
        val longest = "${s.longestSessionMinutes}m"
        return """
            You are an AI digital wellness coach.

            Analyze the following phone usage behavior.

            User Statistics:
            - Total Social Media Time: $totalStr
            - Longest Session: $longest
            - Night Usage: ${if (s.nightUsage) "Yes" else "No"}
            - Most Used App: ${s.mostUsedAppLabel}
            - Doomscrolling Score: ${assessment.score}

            Generate:
            1. Short behavioral analysis
            2. Addiction severity assessment
            3. Productivity impact
            4. Sleep impact
            5. 3 actionable recommendations
            6. Motivational wellness message

            Also add one short line: "AI Reflection:" with a creative comparison (e.g. time spent vs reading a chapter).
            Optional line: "Current Digital Wellness State:" with one word like Focused, Distracted, or Drained.

            Keep response concise, modern, and friendly. Use clear section headers.
        """.trimIndent()
    }

    /**
     * Ask Gemini for JSON only so the app can draw charts, meters, and bars — not paragraphs.
     */
    fun wellbeingGraphicsPrompt(assessment: AddictionAssessment, device: DeviceUsageSnapshot): String {
        val appsJson = JSONArray()
        device.topApps.take(30).forEach { e ->
            appsJson.put(
                JSONObject().apply {
                    put("name", e.displayName)
                    put("min", e.minutesToday)
                    put("pkg", e.packageName)
                },
            )
        }
        val payload = JSONObject().apply {
            put("social_doomscore", assessment.score)
            put("social_risk", assessment.risk.name)
            put("social_top_label", assessment.summary.mostUsedAppLabel)
            put("social_minutes_estimated", assessment.summary.totalSocialMediaMinutes)
            put("total_screen_minutes_today", device.totalScreenMinutesToday)
            put("distinct_apps_used_today", device.distinctPackagesUsedToday)
            put("approx_installed_user_apps", device.approximateInstalledUserApps ?: JSONObject.NULL)
            put("top_apps_today", appsJson)
        }
        return """
            You are a digital-wellbeing analyst. Output ONE JSON object only (no markdown, no backticks, no commentary).
            The phone owner shared real usage stats for wellbeing coaching.

            DEVICE_JSON:
            $payload

            JSON schema — respond with exactly these keys (integers only where noted):
            - headline: max 4 words
            - emoji: one character emoji or empty
            - wellness_score, energy, calm, focus: each 0-100 integer
            - time_balance: array of 3-5 objects {label, percent, tone} where tone is good|warn|alert|neutral; percents sum ~100
            - app_heavyweights: array up to 5 of {app, minutes, heat} heat 1-3
            - spark_words: array of exactly 4 very short phrases (1-2 words)

            Rules: no paragraphs, no markdown, JSON only; derive time_balance categories from the app mix (social vs productivity vs utilities etc.).
        """.trimIndent()
    }
}
