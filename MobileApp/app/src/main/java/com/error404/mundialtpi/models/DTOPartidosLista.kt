package com.error404.mundialtpi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.error404.mundialtpi.utils.generarAvatarUrl

@Serializable
data class DTOPartidosLista(
    val id: Int,
    @SerialName("equipoLocal") val equipo1: String,
    @SerialName("equipoVisitante") val equipo2: String,
    @SerialName("fechaHora") val fecha: String,
    val fase: String
) {
    val flag1: String get() = generarAvatarUrl(equipo1)
    val flag2: String get() = generarAvatarUrl(equipo2)
}