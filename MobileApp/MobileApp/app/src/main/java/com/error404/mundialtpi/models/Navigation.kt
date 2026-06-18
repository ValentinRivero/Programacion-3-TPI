package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
object DestinoLista

@Serializable
data class DestinoDetalle(val partidoId: String)

@Serializable
object DestinoLogin

@Serializable
object DestinoRegistro