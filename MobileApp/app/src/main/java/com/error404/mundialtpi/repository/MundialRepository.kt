package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.DTOPartidosDetalle
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.network.MundialAPIService
import com.error404.mundialtpi.utils.TokenManager // <-- Nuevo import
import retrofit2.HttpException // <-- Nuevo import

class MundialRepository(
    private val api: MundialAPIService,
    private val tokenManager: TokenManager
) {
    suspend fun fetchPartidosLista(): List<DTOPartidosLista> {
        return try {
            api.getPartidosLista()
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                tokenManager.clearToken()
            }
            throw e
        }
    }

    suspend fun fetchPartidosDetalle(id: Int): DTOPartidosDetalle {
        return try {
            api.getPartidosDetalle(id)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                tokenManager.clearToken()
            }
            throw e
        }
    }
}