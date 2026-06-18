package com.error404.mundialtpi.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDateTime(rawDate: String): Pair<String, String> {
    return try {
        val instant = Instant.parse(rawDate)
        val dateTime = instant.atZone(ZoneId.systemDefault())
        val datePart = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val timePart = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        Pair(datePart, timePart)
    } catch (e: Exception) {
        Pair(rawDate, "--:--")
    }
}