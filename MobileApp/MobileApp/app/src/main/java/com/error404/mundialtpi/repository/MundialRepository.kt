package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.DTOPartidosDetalle
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.network.MundialAPIService

class MundialRepository(private val api: MundialAPIService) {
    suspend fun fetchPartidosLista(): List<DTOPartidosLista>{
        return api.getPartidosLista()
    }

    suspend fun fetchPartidosDetalle(id: String): DTOPartidosDetalle {
        return api.getPartidosDetalle(id)
    }
}