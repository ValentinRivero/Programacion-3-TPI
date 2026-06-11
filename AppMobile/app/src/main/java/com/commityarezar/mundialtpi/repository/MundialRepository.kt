package com.commityarezar.mundialtpi.repository

import com.commityarezar.mundialtpi.models.DTOPartidosDetalle
import com.commityarezar.mundialtpi.models.DTOPartidosLista
import com.commityarezar.mundialtpi.network.MundialAPIService

class MundialRepository(private val api: MundialAPIService) {
    suspend fun fetchPartidosLista(): List<DTOPartidosLista>{
        return api.getPartidosLista()
    }

    suspend fun fetchPartidosDetalle(id: String): DTOPartidosDetalle {
        return api.getPartidosDetalle(id)
    }
}