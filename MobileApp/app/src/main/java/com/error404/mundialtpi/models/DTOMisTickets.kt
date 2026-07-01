package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class EstadioMisTickets(
    val nombre: String,
    val ciudad: String
)

@Serializable
data class PartidoMisTickets(
    val equipoLocal: String,
    val equipoVisitante: String,
    val fase: String,
    val fechaHora: String,
    val estadio: EstadioMisTickets? = null
)

@Serializable
data class DTOMisTickets(
    val id: Int,
    val tipoEntrada: Int,
    val activo: Boolean,
    val partido: PartidoMisTickets
) {
    val nombrePartido: String get() = "${partido.equipoLocal} vs ${partido.equipoVisitante}"

    val nombreEstadio: String get() = partido.estadio?.nombre ?: "Estadio a confirmar"

    val fechaPartidoTexto: String get() = partido.fechaHora

    val tipoEntradaTexto: String get() = when (tipoEntrada) {
        1 -> "Categoría 1 - Laterales"
        2 -> "Categoría 2 - Córners"
        3 -> "Categoría 3 - Cabeceras"
        else -> "Categoría General"
    }

    val precioCalculado: Double get() = when (tipoEntrada) {
        1 -> 250.0
        2 -> 150.0
        3 -> 100.0
        else -> 0.0
    }
}