package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOEstadio(
    val id: Int,
    val nombre: String,
    val ciudad: String,
    val pais: String,
    val capacidad: Int,
    val imagenUrl: String
)