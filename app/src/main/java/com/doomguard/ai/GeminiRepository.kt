package com.doomguard.ai

import com.doomguard.BuildConfig
import com.doomguard.domain.AddictionAssessment
import com.doomguard.tracking.DeviceUsageSnapshot
import kotlinx.coroutines.delay
import retrofit2.HttpException

class GeminiRepository(
    private val api: GeminiApi,
) {

    private val models = listOf(
        "gemini-2.0-flash",
        "gemini-1.5-flash",
        "gemini-3-flash-preview",
    )

    suspend fun generateInsight(assessment: AddictionAssessment): Result<String> {
        val prompt = PromptBuilder.wellnessCoachPrompt(assessment)
        val body = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt))),
            ),
        )
        return generateRaw(body)
    }

    suspend fun generateWellbeingGraphics(
        assessment: AddictionAssessment,
        device: DeviceUsageSnapshot,
    ): Result<WellbeingGraphicsPayload> {
        val prompt = PromptBuilder.wellbeingGraphicsPrompt(assessment, device)
        val body = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt))),
            ),
            generationConfig = GeminiGenerationConfig(responseMimeType = "application/json"),
        )
        return generateRaw(body).mapCatching { raw ->
            WellbeingGraphicsParser.parse(raw).getOrElse { throw it }
        }
    }

    private suspend fun generateRaw(body: GeminiGenerateRequest): Result<String> {
        val key = BuildConfig.GEMINI_API_KEY.trim()
        if (key.isBlank()) {
            return Result.failure(IllegalStateException("Add GEMINI_API_KEY to local.properties"))
        }
        var last: Exception? = null
        for ((useV1, label) in listOf(false to "v1beta", true to "v1")) {
            for (model in models) {
                try {
                    val text = callOnce(useV1, model, key, body)
                    if (!text.isNullOrBlank()) {
                        return Result.success(text.trim())
                    }
                    last = IllegalStateException("$label/$model: empty response")
                } catch (e: HttpException) {
                    val snippet = e.response()?.errorBody()?.string().orEmpty().take(400)
                    last = IllegalStateException(
                        "HTTP ${e.code()} ($label, $model). ${e.message}. $snippet",
                    )
                    if (e.code() == 429 || e.code() in 500..599) {
                        delay(600L)
                        try {
                            val text = callOnce(useV1, model, key, body)
                            if (!text.isNullOrBlank()) {
                                return Result.success(text.trim())
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    last = Exception("$label/$model: ${e.message}", e)
                }
            }
        }
        return Result.failure(last ?: IllegalStateException("Could not reach Gemini API"))
    }

    private suspend fun callOnce(
        useV1: Boolean,
        model: String,
        key: String,
        body: GeminiGenerateRequest,
    ): String? {
        val res = if (useV1) {
            api.generateV1(model, key, body)
        } else {
            api.generateV1Beta(model, key, body)
        }
        return res.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
    }
}
