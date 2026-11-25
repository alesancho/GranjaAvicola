package com.example.granjaavicola.domain.calculos

import com.example.granjaavicola.data.entity.RegistroDiario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RegistroLogicaTest {

    private val hoy = LocalDate.of(2026, 6, 15)
    private val llegada = LocalDate.of(2026, 6, 1)

    private fun registro(
        fecha: LocalDate,
        mortalidad: Int = 0,
        descarte: Int = 0
    ) = RegistroDiario(
        id = 1L,
        loteId = 10L,
        fecha = fecha,
        consumoKg = 50.0,
        aguaLitros = 0.0,
        mortalidad = mortalidad,
        descarte = descarte,
        temperaturaMax = 0.0,
        temperaturaMin = 0.0,
        horasLuz = 0.0
    )

    private fun entradaValida(
        esProduccion: Boolean = false,
        avesVivasAntesDeFecha: Int = 900,
        fecha: LocalDate = hoy,
        consumoKg: String = "45.5",
        mortalidad: String = "2",
        descarte: String = "1",
        aguaLitros: String = "",
        temperaturaMax: String = "",
        temperaturaMin: String = "",
        horasLuz: String = "",
        observaciones: String = "  Sin novedad  ",
        huevosAA: String = "300",
        huevosA: String = "250",
        huevosB: String = "100",
        huevosC: String = "50",
        huevosRotos: String = "5",
        huevosSucios: String = "3"
    ) = EntradaFormularioRegistro(
        hayLoteSeleccionado = true,
        fecha = fecha,
        fechaLlegadaLote = llegada,
        esProduccion = esProduccion,
        avesVivasAntesDeFecha = avesVivasAntesDeFecha,
        consumoKg = consumoKg,
        mortalidad = mortalidad,
        descarte = descarte,
        aguaLitros = aguaLitros,
        temperaturaMax = temperaturaMax,
        temperaturaMin = temperaturaMin,
        horasLuz = horasLuz,
        observaciones = observaciones,
        huevosAA = huevosAA,
        huevosA = huevosA,
        huevosB = huevosB,
        huevosC = huevosC,
        huevosRotos = huevosRotos,
        huevosSucios = huevosSucios
    )

    // =========================================================================
    // calcularSaldoRegistro
    // =========================================================================

    @Test
    fun `calcularSaldoRegistro excluye fecha editada y suma bajas digitadas`() {
        val registros = listOf(
            registro(LocalDate.of(2026, 6, 10), mortalidad = 10, descarte = 5),
            registro(LocalDate.of(2026, 6, 14), mortalidad = 20, descarte = 5),
            // Registro existente en la fecha editada (debe excluirse para no contar doble)
            registro(hoy, mortalidad = 8, descarte = 2)
        )

        val resultado = calcularSaldoRegistro(
            avesIniciales = 1000,
            registrosDelLote = registros,
            fechaEditada = hoy,
            mortalidadDigitada = 3,
            descarteDigitado = 2
        )

        // 1000 - (10+20) - (5+5) = 960 antes de hoy
        assertEquals(960, resultado.avesVivasAntesDeFecha)
        // 960 - 3 - 2 = 955 con lo digitado
        assertEquals(955, resultado.avesVivasConDigitado)
    }

    @Test
    fun `calcularSaldoRegistro sin registros previos ni bajas mantiene aves iniciales`() {
        val resultado = calcularSaldoRegistro(
            avesIniciales = 500,
            registrosDelLote = emptyList(),
            fechaEditada = hoy,
            mortalidadDigitada = 0,
            descarteDigitado = 0
        )

        assertEquals(500, resultado.avesVivasAntesDeFecha)
        assertEquals(500, resultado.avesVivasConDigitado)
    }

    @Test
    fun `calcularSaldoRegistro ignora bajas digitadas negativas en el saldo`() {
        val resultado = calcularSaldoRegistro(
            avesIniciales = 500,
            registrosDelLote = emptyList(),
            fechaEditada = hoy,
            mortalidadDigitada = -5,
            descarteDigitado = -2
        )

        assertEquals(500, resultado.avesVivasAntesDeFecha)
        assertEquals(500, resultado.avesVivasConDigitado)
    }

    // =========================================================================
    // validarFechaRegistro y validarFechaTraslado
    // =========================================================================

    @Test
    fun `validarFechaRegistro acepta hoy y fecha de llegada`() {
        assertNull(validarFechaRegistro(hoy, llegada, hoy))
        assertNull(validarFechaRegistro(llegada, llegada, hoy))
    }

    @Test
    fun `validarFechaRegistro rechaza fecha futura y anterior a llegada`() {
        assertNotNull(validarFechaRegistro(hoy.plusDays(1), llegada, hoy))
        assertNotNull(validarFechaRegistro(llegada.minusDays(1), llegada, hoy))
    }

    @Test
    fun `validarFechaTraslado acepta fechas validas y rechaza fuera de rango`() {
        assertNull(validarFechaTraslado(hoy, llegada, hoy))
        assertNull(validarFechaTraslado(llegada, llegada, hoy))
        assertNotNull(validarFechaTraslado(hoy.plusDays(1), llegada, hoy))
        assertNotNull(validarFechaTraslado(llegada.minusDays(1), llegada, hoy))
    }

    // =========================================================================
    // validarFormularioRegistro
    // =========================================================================

    @Test
    fun `validarFormularioRegistro valido con opcionales vacios guarda 0_0`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(esProduccion = false),
            hoy = hoy
        )

        assertTrue(!resultado.errores.tieneErrores)
        val datos = resultado.datos
        assertNotNull(datos)
        assertEquals(45.5, datos!!.consumoKg, 0.001)
        assertEquals(2, datos.mortalidad)
        assertEquals(1, datos.descarte)
        assertEquals(0.0, datos.aguaLitros, 0.001)
        assertEquals(0.0, datos.temperaturaMax, 0.001)
        assertEquals(0.0, datos.temperaturaMin, 0.001)
        assertEquals(0.0, datos.horasLuz, 0.001)
        assertEquals("Sin novedad", datos.observaciones)
    }

    @Test
    fun `validarFormularioRegistro acepta coma decimal en consumo`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(consumoKg = "12,75"),
            hoy = hoy
        )

        assertNotNull(resultado.datos)
        assertEquals(12.75, resultado.datos!!.consumoKg, 0.001)
    }

    @Test
    fun `validarFormularioRegistro rechaza consumo vacio o negativo`() {
        val vacio = validarFormularioRegistro(entradaValida(consumoKg = ""), hoy = hoy)
        assertNotNull(vacio.errores.errorConsumoKg)
        assertNull(vacio.datos)

        val negativo = validarFormularioRegistro(entradaValida(consumoKg = "-1.5"), hoy = hoy)
        assertNotNull(negativo.errores.errorConsumoKg)
        assertNull(negativo.datos)
    }

    @Test
    fun `validarFormularioRegistro rechaza negativos en opcionales y huevos`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(
                esProduccion = true,
                aguaLitros = "-10",
                temperaturaMax = "-1",
                temperaturaMin = "-2",
                horasLuz = "-3",
                huevosAA = "-1",
                huevosA = "-1",
                huevosB = "-1",
                huevosC = "-1",
                huevosRotos = "-1",
                huevosSucios = "-1"
            ),
            hoy = hoy
        )

        assertNull(resultado.datos)
        assertNotNull(resultado.errores.errorAguaLitros)
        assertNotNull(resultado.errores.errorTemperaturaMax)
        assertNotNull(resultado.errores.errorTemperaturaMin)
        assertNotNull(resultado.errores.errorHorasLuz)
        assertNotNull(resultado.errores.errorHuevosAA)
        assertNotNull(resultado.errores.errorHuevosA)
        assertNotNull(resultado.errores.errorHuevosB)
        assertNotNull(resultado.errores.errorHuevosC)
        assertNotNull(resultado.errores.errorHuevosRotos)
        assertNotNull(resultado.errores.errorHuevosSucios)
    }

    @Test
    fun `validarFormularioRegistro ignora huevos invalidos si el lote no esta en produccion`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(
                esProduccion = false,
                huevosAA = "-5",
                huevosRotos = "abc"
            ),
            hoy = hoy
        )

        assertNotNull(resultado.datos)
        assertNull(resultado.errores.errorHuevosAA)
        assertNull(resultado.errores.errorHuevosRotos)
    }

    @Test
    fun `validarFormularioRegistro rechaza mortalidad mas descarte mayor a aves vivas`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(
                avesVivasAntesDeFecha = 10,
                mortalidad = "6",
                descarte = "5"
            ),
            hoy = hoy
        )

        assertNull(resultado.datos)
        assertNotNull(resultado.errores.errorMortalidad)
        assertNotNull(resultado.errores.errorDescarte)
    }

    @Test
    fun `validarFormularioRegistro permite mortalidad mas descarte igual a aves vivas`() {
        val resultado = validarFormularioRegistro(
            entrada = entradaValida(
                avesVivasAntesDeFecha = 10,
                mortalidad = "6",
                descarte = "4"
            ),
            hoy = hoy
        )

        assertNotNull(resultado.datos)
    }
}
