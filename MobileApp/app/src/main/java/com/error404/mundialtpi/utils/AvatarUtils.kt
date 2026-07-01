package com.error404.mundialtpi.utils
fun generarAvatarUrl(pais: String): String {
    val nombreLimpio = pais.lowercase().trim()

    val codigoIso = when (nombreLimpio) {
        "argentina" -> "ar"
        "brasil" -> "br"
        "francia" -> "fr"
        "españa" -> "es"
        "alemania" -> "de"
        "inglaterra" -> "gb-eng"
        "portugal" -> "pt"
        "uruguay" -> "uy"
        "colombia" -> "co"
        "mexico", "méxico" -> "mx"
        "estados unidos" -> "us"
        "italia" -> "it"
        "paises bajos", "países bajos", "holanda" -> "nl"
        "croacia" -> "hr"
        "belgica", "bélgica" -> "be"
        "japon", "japón" -> "jp"
        "corea del sur" -> "kr"
        "marruecos" -> "ma"
        "ecuador" -> "ec"
        "chile" -> "cl"
        "peru", "perú" -> "pe"
        "paraguay" -> "py"
        "venezuela" -> "ve"
        "bolivia" -> "bo"
        "canada", "canadá" -> "ca"
        "rusia" -> "ru"
        else -> "un"
    }

    if (codigoIso == "un") {
        return "https://ui-avatars.com/api/?name=${pais}&background=2C3E50&color=fff&bold=true"
    }

    return "https://flagcdn.com/w160/$codigoIso.png"
}