package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOMisTickets(
    val id: Int,
    val partido: String,
    val estadio: String,
    val fechaPartido: String,
    val tipoEntrada: String,
    val precio: Double,
    val fechaCompra: String
)