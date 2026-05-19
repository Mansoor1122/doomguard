package com.doomguard.ai

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateV1Beta(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body body: GeminiGenerateRequest,
    ): GeminiGenerateResponse

    @POST("v1/models/{model}:generateContent")
    suspend fun generateV1(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body body: GeminiGenerateRequest,
    ): GeminiGenerateResponse
}

object GeminiClient {
    private const val BASE = "https://generativelanguage.googleapis.com/"

    fun create(debug: Boolean): GeminiApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}
