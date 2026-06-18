package com.error404.mundialtpi.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }
    val api: MundialAPIService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5021/api/")
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(MundialAPIService::class.java)
    }
}