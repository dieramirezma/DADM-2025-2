package com.example.reto3.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensiones útiles para el proyecto
 */

/**
 * Formatea un timestamp a formato legible
 */
fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Formatea un timestamp a formato de fecha completa
 */
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Convierte timestamp a tiempo relativo (hace 5 minutos, etc)
 */
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "Hace un momento"
        diff < 3600_000 -> "Hace ${diff / 60_000} min"
        diff < 86400_000 -> "Hace ${diff / 3600_000} h"
        else -> "Hace ${diff / 86400_000} días"
    }
}

/**
 * Genera un ID único para jugadores
 */
fun generatePlayerId(): String {
    return UUID.randomUUID().toString()
}

/**
 * Valida si un nombre de jugador es válido
 */
fun String.isValidPlayerName(): Boolean {
    return this.isNotBlank() && this.length in 2..20
}

/**
 * Sanitiza un nombre de jugador
 */
fun String.sanitizePlayerName(): String {
    return this.trim()
        .replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]"), "")
        .take(20)
}
