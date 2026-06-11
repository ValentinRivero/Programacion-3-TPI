package com.error404.mundialtpi.network

import com.error404.mundialtpi.models.DTOPartidosDetalle
import com.error404.mundialtpi.models.DTOPartidosLista
import retrofit2.http.GET
import retrofit2.http.Path

interface MundialAPIService {

    @GET("Partidos")
    suspend fun getPartidosLista(): List<DTOPartidosLista>

    @GET("Partidos/{id}")
    suspend fun getPartidosDetalle(@Path("id") id: String): DTOPartidosDetalle
}