package com.error404.mundialtpi.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDateTime(fechaIso: String): Pair<String, String> {
    return try {
        val fechaLimpia = fechaIso.substringBefore(".").substringBefore("Z")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(fechaLimpia)

        if (date != null) {
            val formatterFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatterHora = SimpleDateFormat("HH:mm", Locale.getDefault())
            Pair(formatterFecha.format(date), formatterHora.format(date))
        } else {
            Pair("Fecha a confirmar", "--:--")
        }
    } catch (e: Exception) {
        Pair(fechaIso.substringBefore("T"), fechaIso.substringAfter("T").take(5))
    }
}