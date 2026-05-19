package com.doomguard.ai

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object WellbeingGraphicsParser {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter by lazy { moshi.adapter(WellbeingGraphicsPayload::class.java) }

    fun parse(raw: String): Result<WellbeingGraphicsPayload> = runCatching {
        var t = raw.trim()
        if (t.startsWith("```")) {
            t = t.removePrefix("```json").removePrefix("```JSON").removePrefix("```").trim()
            val endFence = t.lastIndexOf("```")
            if (endFence >= 0) t = t.substring(0, endFence).trim()
        }
        val start = t.indexOf('{')
        val end = t.lastIndexOf('}')
        if (start < 0 || end <= start) {
            error("No JSON object in model output")
        }
        val json = t.substring(start, end + 1)
        adapter.fromJson(json) ?: error("Moshi returned null")
    }
}
