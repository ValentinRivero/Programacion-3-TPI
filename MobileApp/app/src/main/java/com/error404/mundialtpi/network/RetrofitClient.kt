package com.error404.mundialtpi.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }
    val api: MundialAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://6a2a4956b687a7d5cbc372ce.mockapi.io/api/test/")
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(MundialAPIService::class.java)
    }
}