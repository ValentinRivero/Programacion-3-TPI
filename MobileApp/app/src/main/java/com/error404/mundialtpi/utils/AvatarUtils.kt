package com.error404.mundialtpi.utils

import java.net.URLEncoder

fun generarAvatarUrl(nombreEquipo: String): String {
    if (nombreEquipo.isBlank()) return "https://ui-avatars.com/api/?name=X&background=random"

    val nombreSeguro = URLEncoder.encode(nombreEquipo, "UTF-8")
    return "https://ui-avatars.com/api/?name=${nombreSeguro}&background=random&size=128"
}