package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class ComprarTicketRequest(
    val partidoId: Int,
    val categoriaId: Int,
    val cantidad: Int
)

@Serializable
data class TicketResponse(
    val id: Int? = null,
    val ticketId: Int? = null,
    val resumen: String? = null,
    val tipoEntrada: String? = null
)