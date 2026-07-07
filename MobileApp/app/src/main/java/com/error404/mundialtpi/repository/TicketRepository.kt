package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.ComprarTicketRequest
import com.error404.mundialtpi.models.TicketResponse
import com.error404.mundialtpi.network.MundialAPIService
import com.error404.mundialtpi.utils.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException

class TicketRepository(
    private val api: MundialAPIService,
    private val tokenManager: TokenManager
) {
    suspend fun comprarTicket(partidoId: Int, categoriaId: Int, cantidad: Int): Result<TicketResponse> {
        val token = tokenManager.tokenFlow.firstOrNull() ?: ""
        val bearerToken = "Bearer $token"

        return try {
            val response = api.comprarTicket(
                bearerToken,
                ComprarTicketRequest(partidoId, categoriaId, cantidad)
            )
            Result.success(response)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                tokenManager.clearToken()
                Result.failure(Exception("Tu sesión expiró. Por favor, volvé a ingresar."))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getMisTickets(): Result<List<com.error404.mundialtpi.models.DTOMisTickets>> {
        val token = tokenManager.tokenFlow.firstOrNull() ?: ""
        val bearerToken = "Bearer $token"

        return try {
            val response = api.getMisTickets(bearerToken)
            Result.success(response)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                tokenManager.clearToken()
                Result.failure(Exception("Tu sesión expiró. Por favor, volvé a ingresar."))
            } else {
                Result.failure(e)
            }
        }
    }
}