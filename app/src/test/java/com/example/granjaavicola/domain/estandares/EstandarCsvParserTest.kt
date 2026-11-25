package com.example.granjaavicola.domain.estandares

import com.example.granjaavicola.data.entity.FaseEstandar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EstandarCsvParserTest {

    @Test
    fun `fila normal de produccion se parsea con todos sus campos`() {
        val csv = """
            linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g
            ISA_BROWN_JAULA,24,PRODUCCION,1765.0,111.0,95.4,57.3
        """.trimIndent()

        val resultado = parsearEstandaresCsv(csv)

        assertEquals(1, resultado.size)
        val estandar = resultado.first()
        assertEquals("ISA_BROWN_JAULA", estandar.linea)
        assertEquals(24, estandar.semanaEdad)
        assertEquals(FaseEstandar.PRODUCCION, estandar.fase)
        assertEquals(1765.0, estandar.pesoAveG!!, 0.0001)
        assertEquals(111.0, estandar.consumoGAveDia!!, 0.0001)
        assertEquals(95.4, estandar.posturaPct!!, 0.0001)
        assertEquals(57.3, estandar.pesoHuevoG!!, 0.0001)
    }

    @Test
    fun `campos vacios se convierten en null`() {
        val csv = """
            linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g
            ISA_BROWN_PISO,1,LEVANTE,65.5,11,,
            ISA_BROWN_JAULA,5,LEVANTE,,,,
        """.trimIndent()

        val resultado = parsearEstandaresCsv(csv)

        assertEquals(2, resultado.size)

        val primero = resultado[0]
        assertEquals("ISA_BROWN_PISO", primero.linea)
        assertEquals(1, primero.semanaEdad)
        assertEquals(FaseEstandar.LEVANTE, primero.fase)
        assertEquals(65.5, primero.pesoAveG!!, 0.0001)
        assertEquals(11.0, primero.consumoGAveDia!!, 0.0001)
        assertNull(primero.posturaPct)
        assertNull(primero.pesoHuevoG)

        val segundo = resultado[1]
        assertEquals("ISA_BROWN_JAULA", segundo.linea)
        assertEquals(5, segundo.semanaEdad)
        assertEquals(FaseEstandar.LEVANTE, segundo.fase)
        assertNull(segundo.pesoAveG)
        assertNull(segundo.consumoGAveDia)
        assertNull(segundo.posturaPct)
        assertNull(segundo.pesoHuevoG)
    }

    @Test
    fun `filas invalidas y vacias se omiten manteniendo las validas`() {
        val csv = listOf(
            "linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g",
            "",
            "   ",
            "ISA_BROWN_PISO,10,LEVANTE,909,58,,",
            "ISA_BROWN_PISO,abc,LEVANTE,909,58,,",
            "ISA_BROWN_PISO,0,LEVANTE,909,58,,",
            "ISA_BROWN_PISO,-3,LEVANTE,909,58,,",
            ",10,LEVANTE,909,58,,",
            "ISA_BROWN_PISO,10,OTRA_FASE,909,58,,",
            "ISA_BROWN_PISO,10,LEVANTE,no_numero,58,,",
            "ISA_BROWN_PISO,10,LEVANTE,909,-5,,",
            "ISA_BROWN_PISO,10,LEVANTE,909,58",
            "ISA_BROWN_PISO,10,LEVANTE,909,58,1,2,extra",
            "ISA_BROWN_JAULA,18,LEVANTE,1475.0,89.0,,",
            "ISA_BROWN_JAULA,18,PRODUCCION,1475.0,89.0,1.0,42.5"
        ).joinToString("\n")

        val resultado = parsearEstandaresCsv(csv)

        assertEquals(3, resultado.size)
        assertEquals(10, resultado[0].semanaEdad)
        assertEquals(FaseEstandar.LEVANTE, resultado[0].fase)
        assertEquals(18, resultado[1].semanaEdad)
        assertEquals(FaseEstandar.LEVANTE, resultado[1].fase)
        assertEquals(18, resultado[2].semanaEdad)
        assertEquals(FaseEstandar.PRODUCCION, resultado[2].fase)
    }

    @Test
    fun `soporta saltos de linea CRLF y BOM`() {
        val csv = "\uFEFF" +
            "linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g\r\n" +
            "ISA_BROWN_PISO,18,LEVANTE,1576,84,,\r\n" +
            "\r\n" +
            "ISA_BROWN_PISO,18,PRODUCCION,1576,85,3.8,42.3\r\n"

        val resultado = parsearEstandaresCsv(csv)

        assertEquals(2, resultado.size)
        assertEquals(FaseEstandar.LEVANTE, resultado[0].fase)
        assertEquals(84.0, resultado[0].consumoGAveDia!!, 0.0001)
        assertNull(resultado[0].posturaPct)
        assertNull(resultado[0].pesoHuevoG)

        assertEquals(FaseEstandar.PRODUCCION, resultado[1].fase)
        assertEquals(85.0, resultado[1].consumoGAveDia!!, 0.0001)
        assertEquals(3.8, resultado[1].posturaPct!!, 0.0001)
        assertEquals(42.3, resultado[1].pesoHuevoG!!, 0.0001)
    }

    @Test
    fun `csv vacio o solo encabezado devuelve lista vacia`() {
        assertTrue(parsearEstandaresCsv("").isEmpty())
        assertTrue(parsearEstandaresCsv("   \r\n  ").isEmpty())
        assertTrue(
            parsearEstandaresCsv(
                "linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g\r\n"
            ).isEmpty()
        )
    }
}
