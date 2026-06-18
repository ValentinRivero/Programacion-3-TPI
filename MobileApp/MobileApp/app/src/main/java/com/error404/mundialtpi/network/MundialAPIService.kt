package com.error404.mundialtpi.network

import com.error404.mundialtpi.models.DTOPartidosDetalle
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.models.LoginRequest
import com.error404.mundialtpi.models.LoginResponse
import com.error404.mundialtpi.models.RegistroRequest
import com.error404.mundialtpi.models.RegistroResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MundialAPIService {

    @GET("Partidos")
    suspend fun getPartidosLista(): List<DTOPartidosLista>

    @GET("Partidos/{id}")
    suspend fun getPartidosDetalle(@Path("id") id: String): DTOPartidosDetalle

    @POST("Auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("Auth/registro")
    suspend fun registro(@Body request: RegistroRequest): RegistroResponse
}