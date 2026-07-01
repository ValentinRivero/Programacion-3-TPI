package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.ComprarTicketRequest
import com.error404.mundialtpi.models.TicketResponse
import com.error404.mundialtpi.network.MundialAPIService
import com.error404.mundialtpi.utils.TokenManager

class TicketRepository(
    private val api: MundialAPIService,
    private val tokenManager: TokenManager
) {
    suspend fun comprarTicket(partidoId: Int, categoriaId: Int, cantidad: Int): Result<TicketResponse> {
        val token = tokenManager.getToken() ?: return Result.failure(Exception("No autenticado"))
        val bearerToken = "Bearer $token"

        return try {
            val response = api.comprarTicket(
                bearerToken,
                ComprarTicketRequest(partidoId, categoriaId, cantidad)
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisTickets(): Result<List<com.error404.mundialtpi.models.DTOMisTickets>> {
        val token = tokenManager.getToken() ?: return Result.failure(Exception("No autenticado"))
        val bearerToken = "Bearer $token"

        return try {
            val response = api.getMisTickets(bearerToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
