package com.commityarezar.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOPartidosDetalle(
    val id: Int,
    val equipo1: String,
    val equipo2: String,
    val grupo: String,
    val fecha: String,
    val flag1: String,
    val flag2: String,
    val precio: String,
    val estadio: String
)