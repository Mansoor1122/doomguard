package com.doomguard.ai

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
)

data class GeminiGenerationConfig(
    val responseMimeType: String? = "application/json",
)

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>,
)

data class GeminiPart(
    val text: String,
)

data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiErrorBody? = null,
)

data class GeminiCandidate(
    val content: GeminiContentResponse?,
)

data class GeminiContentResponse(
    val parts: List<GeminiPart>?,
)

data class GeminiErrorBody(
    val message: String? = null,
)
