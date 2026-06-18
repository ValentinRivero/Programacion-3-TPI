package com.error404.mundialtpi.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDateTime(rawDate: String): Pair<String, String> {
    return try {
        val parsedDate = LocalDateTime.parse(rawDate)
        val datePart = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val timePart = parsedDate.format(DateTimeFormatter.ofPattern("HH:mm"))
        Pair(datePart, timePart)
    } catch (e: Exception) {
        Pair(rawDate, "--:--")
    }
}