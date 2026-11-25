package com.example.granjaavicola.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class FormatoUtilTest {

    // ─── formatearCOP ───────────────────────────────────────────────────────────

    @Test fun `formatearCOP devuelve signo y punto de miles`() {
        assertEquals("\$1.250.000", formatearCOP(1_250_000L))
    }

    @Test fun `formatearCOP con cero devuelve cero`() {
        assertEquals("\$0", formatearCOP(0L))
    }

    @Test fun `formatearCOP con valor menor a mil sin punto`() {
        assertEquals("\$500", formatearCOP(500L))
    }

    @Test fun `formatearCOP con millones multiples`() {
        assertEquals("\$12.500.000", formatearCOP(12_500_000L))
    }

    @Test fun `formatearCOP con exactamente mil`() {
        assertEquals("\$1.000", formatearCOP(1_000L))
    }

    @Test fun `formatearCOP con valor negativo`() {
        assertEquals("-\$1.250.000", formatearCOP(-1_250_000L))
    }

    // ─── formatearFecha ─────────────────────────────────────────────────────────

    @Test fun `formatearFecha formato dd MM yyyy`() {
        val fecha = LocalDate.of(2024, 3, 5)
        assertEquals("05/03/2024", formatearFecha(fecha))
    }

    @Test fun `formatearFecha con dia y mes de dos digitos`() {
        val fecha = LocalDate.of(2023, 12, 31)
        assertEquals("31/12/2023", formatearFecha(fecha))
    }

    @Test fun `formatearFecha primer dia del anio`() {
        val fecha = LocalDate.of(2025, 1, 1)
        assertEquals("01/01/2025", formatearFecha(fecha))
    }

    // ─── parsearFecha ────────────────────────────────────────────────────────────

    @Test fun `parsearFecha cadena valida devuelve LocalDate`() {
        val resultado = parsearFecha("05/03/2024")
        assertEquals(LocalDate.of(2024, 3, 5), resultado)
    }

    @Test fun `parsearFecha cadena vacia devuelve null`() {
        assertNull(parsearFecha(""))
    }

    @Test fun `parsearFecha cadena solo espacios devuelve null`() {
        assertNull(parsearFecha("   "))
    }

    @Test fun `parsearFecha formato incorrecto devuelve null`() {
        assertNull(parsearFecha("2024-03-05"))
    }

    @Test fun `parsearFecha formato con guiones devuelve null`() {
        assertNull(parsearFecha("05-03-2024"))
    }

    @Test fun `parsearFecha redondeo ida y vuelta`() {
        val original = LocalDate.of(2024, 7, 15)
        val texto = formatearFecha(original)
        val resultado = parsearFecha(texto)
        assertEquals(original, resultado)
    }
}
