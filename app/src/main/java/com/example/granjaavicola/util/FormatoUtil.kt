package com.example.granjaavicola.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/** Formato de fecha estándar dd/MM/yyyy usado en toda la app. */
val FORMATO_FECHA: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "CO"))

/**
 * Formatea un valor Long como pesos colombianos sin decimales.
 * Ejemplo: 1250000 → "$1.250.000"
 */
fun formatearCOP(valor: Long): String {
    if (valor == 0L) return "\$0"
    val negativo = valor < 0
    val abs = if (negativo) -valor else valor
    val digits = abs.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in digits.indices.reversed()) {
        if (count > 0 && count % 3 == 0) sb.insert(0, '.')
        sb.insert(0, digits[i])
        count++
    }
    return if (negativo) "-\$$sb" else "\$$sb"
}

/**
 * Formatea un [LocalDate] como "dd/MM/yyyy".
 * Ejemplo: LocalDate.of(2024, 3, 5) → "05/03/2024"
 */
fun formatearFecha(fecha: LocalDate): String = fecha.format(FORMATO_FECHA)

/**
 * Parsea una cadena "dd/MM/yyyy" a [LocalDate].
 * Devuelve null si el texto está vacío o tiene formato incorrecto.
 */
fun parsearFecha(texto: String): LocalDate? {
    if (texto.isBlank()) return null
    return try {
        LocalDate.parse(texto.trim(), FORMATO_FECHA)
    } catch (_: DateTimeParseException) {
        null
    }
}
