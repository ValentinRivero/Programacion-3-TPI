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

    @GET("partidos")
    suspend fun getPartidosLista(): List<DTOPartidosLista>

    @GET("partidos/{id}")
    suspend fun getPartidosDetalle(@Path("id") id: Int): DTOPartidosDetalle

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registro(@Body request: RegistroRequest): RegistroResponse

    @POST("tickets/comprar")
    suspend fun comprarTicket(
        @retrofit2.http.Header("Authorization") token: String,
        @Body request: com.error404.mundialtpi.models.ComprarTicketRequest
    ): com.error404.mundialtpi.models.TicketResponse

    @GET("tickets/mis-tickets")
    suspend fun getMisTickets(
        @retrofit2.http.Header("Authorization") token: String
    ): List<com.error404.mundialtpi.models.DTOMisTickets>
}