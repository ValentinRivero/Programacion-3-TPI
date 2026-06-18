package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class ComprarTicketRequest(
    val partidoId: Int,
    val tipoEntrada: String,
    val cantidad: Int
)

@Serializable
data class TicketResponse(
    val ticketId: Int,
    val resumen: String
)
