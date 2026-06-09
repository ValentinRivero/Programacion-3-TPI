package com.commityarezar.mundialtpi.network

import com.commityarezar.mundialtpi.models.DTOPartidosDetalle
import com.commityarezar.mundialtpi.models.DTOPartidosLista
import retrofit2.http.GET

interface MundialAPIService {

    @GET("PartidosLista")
    suspend fun getPartidosLista(): List<DTOPartidosLista>

    @GET("PartidosDetalle")
    suspend fun getPartidosDetalle(): List<DTOPartidosDetalle>

}