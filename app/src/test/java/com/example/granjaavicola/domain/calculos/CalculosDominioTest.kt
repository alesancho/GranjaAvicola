package com.example.granjaavicola.domain.calculos

import com.example.granjaavicola.domain.modelos.MovimientoAlimento
import com.example.granjaavicola.domain.modelos.TipoMovimiento
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas unitarias JUnit para los cálculos de dominio avícola.
 * Se ejecutan en la JVM sin dependencias de Android ni de Room.
 */
class CalculosDominioTest {

    // =========================================================================
    // 1. edadEnSemanas
    // =========================================================================

    @Test
    fun edadEnSemanas_diaDeNacimiento_esSemanaUno() {
        // Ejemplo: Nacimiento 2026-01-01, consulta el mismo día (día 0 transcurrido).
        // Semana 1 abarca días 0 a 6, por lo que debe devolver 1.
        val nacimiento = LocalDate.of(2026, 1, 1)
        val fecha = LocalDate.of(2026, 1, 1)

        val resultado = edadEnSemanas(nacimiento, fecha)

        assertEquals(1, resultado)
    }

    @Test
    fun edadEnSemanas_limitesDeSemanas_calculaCorrectamente() {
        val nacimiento = LocalDate.of(2026, 1, 1)

        // Día 6 (2026-01-07): Último día de la Semana 1 -> devuelve 1
        assertEquals(1, edadEnSemanas(nacimiento, LocalDate.of(2026, 1, 7)))

        // Día 7 (2026-01-08): Primer día de la Semana 2 -> devuelve 2
        assertEquals(2, edadEnSemanas(nacimiento, LocalDate.of(2026, 1, 8)))

        // Día 13 (2026-01-14): Último día de la Semana 2 -> devuelve 2
        assertEquals(2, edadEnSemanas(nacimiento, LocalDate.of(2026, 1, 14)))

        // Día 14 (2026-01-15): Primer día de la Semana 3 -> devuelve 3
        assertEquals(3, edadEnSemanas(nacimiento, LocalDate.of(2026, 1, 15)))

        // Día 49 (2026-02-19): 7 semanas completas (49 días) -> entra a Semana 8
        assertEquals(8, edadEnSemanas(nacimiento, LocalDate.of(2026, 2, 19)))
    }

    @Test
    fun edadEnSemanas_fechaAnteriorAlNacimiento_devuelveCero() {
        // Ejemplo: Nacimiento 2026-01-10, fecha consultada 2026-01-05 (5 días antes).
        // Por regla de negocio, si la fecha es anterior al nacimiento devuelve 0.
        val nacimiento = LocalDate.of(2026, 1, 10)
        val fechaAnterior = LocalDate.of(2026, 1, 5)

        val resultado = edadEnSemanas(nacimiento, fechaAnterior)

        assertEquals(0, resultado)
    }

    // =========================================================================
    // 2. saldoAves
    // =========================================================================

    @Test
    fun saldoAves_casoNormal_restaBajasYVentas() {
        // Ejemplo: 10,000 iniciales - 150 muertes - 50 descartes - 200 vendidas = 9,600 aves vivas.
        val resultado = saldoAves(
            avesIniciales = 10000,
            mortalidadAcumulada = 150,
            descarteAcumulado = 50,
            avesVendidas = 200
        )

        assertEquals(9600, resultado)
    }

    @Test
    fun saldoAves_bajasSuperanIniciales_nuncaDevuelveNegativo() {
        // Ejemplo: 500 iniciales - 400 muertes - 100 descartes - 50 vendidas = -50.
        // La regla indica que nunca devuelve negativo, por lo que debe dar 0.
        val resultado = saldoAves(
            avesIniciales = 500,
            mortalidadAcumulada = 400,
            descarteAcumulado = 100,
            avesVendidas = 50
        )

        assertEquals(0, resultado)
    }

    @Test
    fun saldoAves_sinMovimientos_conservaSaldoInicial() {
        // Ejemplo: 5,000 iniciales sin muertes, descartes ni ventas = 5,000.
        val resultado = saldoAves(
            avesIniciales = 5000,
            mortalidadAcumulada = 0,
            descarteAcumulado = 0,
            avesVendidas = 0
        )

        assertEquals(5000, resultado)
    }

    // =========================================================================
    // 3. porcentajeMortalidadSemanal
    // =========================================================================

    @Test
    fun porcentajeMortalidadSemanal_casoNormal_calculaPorcentaje() {
        // Ejemplo: 25 muertes en la semana sobre 1,000 aves iniciales = (25 / 1000) * 100 = 2.5%.
        val resultado = porcentajeMortalidadSemanal(muertesSemana = 25, saldoInicioSemana = 1000)

        assertEquals(2.5, resultado, 0.0001)
    }

    @Test
    fun porcentajeMortalidadSemanal_saldoCero_devuelveCeroSinExcepcion() {
        // Ejemplo: saldo inicial 0 con 5 muertes. Para evitar división por cero debe devolver 0.0.
        val resultado = porcentajeMortalidadSemanal(muertesSemana = 5, saldoInicioSemana = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun porcentajeMortalidadSemanal_ceroMuertes_devuelveCero() {
        // Ejemplo: 0 muertes con 8,000 aves = (0 / 8000) * 100 = 0.0%.
        val resultado = porcentajeMortalidadSemanal(muertesSemana = 0, saldoInicioSemana = 8000)

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 4. pesoPromedio
    // =========================================================================

    @Test
    fun pesoPromedio_muestraEstandar_calculaPromedioAritmetico() {
        // Ejemplo: Pesos [1450.0, 1500.0, 1550.0]. Suma = 4500.0 / 3 = 1500.0 g.
        val pesos = listOf(1450.0, 1500.0, 1550.0)

        val resultado = pesoPromedio(pesos)

        assertEquals(1500.0, resultado, 0.0001)
    }

    @Test
    fun pesoPromedio_listaVacia_devuelveCero() {
        // Ejemplo: Muestra vacía. Regla: lista vacía devuelve 0.0 sin excepción.
        val resultado = pesoPromedio(emptyList())

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun pesoPromedio_unSoloDato_devuelveEseDato() {
        // Ejemplo: Solo 1 ave pesada con 1620.0 g. Promedio = 1620.0 g.
        val resultado = pesoPromedio(listOf(1620.0))

        assertEquals(1620.0, resultado, 0.0001)
    }

    // =========================================================================
    // 5. uniformidadPct
    // =========================================================================

    @Test
    fun uniformidadPct_ejemploDiezPesosResueltoAMano_daOchentaPorCiento() {
        // Ejemplo de 10 pesos resuelto a mano paso a paso:
        // Muestra: [1450.0, 1500.0, 1520.0, 1480.0, 1510.0, 1490.0, 1300.0, 1680.0, 1500.0, 1510.0]
        // 1. Suma total = 14,940.0 g
        // 2. Promedio = 14,940.0 / 10 = 1494.0 g
        // 3. Margen ±10%:
        //    - Límite inferior (-10%): 1494.0 * 0.90 = 1344.6 g
        //    - Límite superior (+10%): 1494.0 * 1.10 = 1643.4 g
        // 4. Verificación de cada peso en el rango [1344.6, 1643.4]:
        //    - 1450.0 -> Sí
        //    - 1500.0 -> Sí
        //    - 1520.0 -> Sí
        //    - 1480.0 -> Sí
        //    - 1510.0 -> Sí
        //    - 1490.0 -> Sí
        //    - 1300.0 -> NO (< 1344.6)
        //    - 1680.0 -> NO (> 1643.4)
        //    - 1500.0 -> Sí
        //    - 1510.0 -> Sí
        // 5. Total aves dentro del rango: 8 de 10.
        // 6. Porcentaje de uniformidad = (8 / 10) * 100 = 80.0%.
        val pesos = listOf(
            1450.0, 1500.0, 1520.0, 1480.0, 1510.0,
            1490.0, 1300.0, 1680.0, 1500.0, 1510.0
        )

        val resultado = uniformidadPct(pesos)

        assertEquals(80.0, resultado, 0.0001)
    }

    @Test
    fun uniformidadPct_todosPesosIguales_daCienPorCiento() {
        // Ejemplo: [1500.0, 1500.0, 1500.0, 1500.0]. Promedio = 1500.0 g.
        // Rango [1350.0, 1650.0]. Las 4 aves están en rango -> (4 / 4) * 100 = 100.0%.
        val pesos = listOf(1500.0, 1500.0, 1500.0, 1500.0)

        val resultado = uniformidadPct(pesos)

        assertEquals(100.0, resultado, 0.0001)
    }

    @Test
    fun uniformidadPct_listaVacia_devuelveCero() {
        // Lista vacía debe retornar 0.0 sin lanzar excepción.
        val resultado = uniformidadPct(emptyList())

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 6. coeficienteVariacionPct
    // =========================================================================

    @Test
    fun coeficienteVariacionPct_tresDatosCalculables_calculaCorrectamente() {
        // Ejemplo numérico exacto: [10.0, 20.0, 30.0]
        // 1. Promedio = (10 + 20 + 30) / 3 = 20.0
        // 2. Diferencias al cuadrado: (10-20)^2 = 100, (20-20)^2 = 0, (30-20)^2 = 100. Suma = 200.0
        // 3. Varianza muestral (n-1 = 2): 200.0 / 2 = 100.0
        // 4. Desviación estándar muestral = sqrt(100.0) = 10.0
        // 5. %CV = (10.0 / 20.0) * 100 = 50.0%
        val pesos = listOf(10.0, 20.0, 30.0)

        val resultado = coeficienteVariacionPct(pesos)

        assertEquals(50.0, resultado, 0.0001)
    }

    @Test
    fun coeficienteVariacionPct_menosDeDosDatos_devuelveCero() {
        // Regla: con menos de 2 datos devuelve 0.0.
        // Probamos con lista vacía y lista de 1 solo dato.
        assertEquals(0.0, coeficienteVariacionPct(emptyList()), 0.0)
        assertEquals(0.0, coeficienteVariacionPct(listOf(1500.0)), 0.0)
    }

    @Test
    fun coeficienteVariacionPct_muestraHomogenea_devuelveCero() {
        // Ejemplo: Pesos idénticos [1500.0, 1500.0, 1500.0].
        // Desviación estándar = 0.0 -> %CV = 0.0%.
        val pesos = listOf(1500.0, 1500.0, 1500.0)

        val resultado = coeficienteVariacionPct(pesos)

        assertEquals(0.0, resultado, 0.0001)
    }

    // =========================================================================
    // 7. consumoGramosPorAveDia
    // =========================================================================

    @Test
    fun consumoGramosPorAveDia_casoNormal_calculaGramosPorAve() {
        // Ejemplo: 110.0 kg consumidos en el día para 1,000 aves vivas.
        // Gramos totales = 110.0 * 1000 = 110,000.0 g.
        // Consumo por ave = 110,000.0 / 1000 = 110.0 g/ave/día.
        val resultado = consumoGramosPorAveDia(consumoKg = 110.0, avesVivas = 1000)

        assertEquals(110.0, resultado, 0.0001)
    }

    @Test
    fun consumoGramosPorAveDia_avesCero_devuelveCeroSinExcepcion() {
        // Ejemplo: 50.0 kg con 0 aves vivas (división por cero evitada) -> 0.0.
        val resultado = consumoGramosPorAveDia(consumoKg = 50.0, avesVivas = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun consumoGramosPorAveDia_consumoCero_devuelveCero() {
        // Ejemplo: 0.0 kg consumidos con 2,000 aves vivas -> 0.0 g/ave/día.
        val resultado = consumoGramosPorAveDia(consumoKg = 0.0, avesVivas = 2000)

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 8. porcentajePostura
    // =========================================================================

    @Test
    fun porcentajePostura_casoNormal_calculaPorcentaje() {
        // Ejemplo: 950 huevos recolectados en el día de 1,000 aves vivas.
        // % postura = (950 / 1000) * 100 = 95.0%.
        val resultado = porcentajePostura(huevosDia = 950, avesVivas = 1000)

        assertEquals(95.0, resultado, 0.0001)
    }

    @Test
    fun porcentajePostura_avesCero_devuelveCeroSinExcepcion() {
        // Ejemplo: 800 huevos con 0 aves vivas (división por cero) -> devuelve 0.0.
        val resultado = porcentajePostura(huevosDia = 800, avesVivas = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun porcentajePostura_ceroHuevos_devuelveCero() {
        // Ejemplo: 0 huevos recolectados de 500 aves vivas = (0 / 500) * 100 = 0.0%.
        val resultado = porcentajePostura(huevosDia = 0, avesVivas = 500)

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 9. conversionKgPorDocena y conversionKgPorKgHuevo
    // =========================================================================

    @Test
    fun conversionKgPorDocena_casoNormal_calculaConversion() {
        // Ejemplo: 150.0 kg de alimento consumidos y 1,200 huevos recolectados.
        // Docenas = 1,200 / 12 = 100 docenas.
        // Conversión = 150.0 / 100 = 1.5 kg/docena.
        val resultado = conversionKgPorDocena(consumoKg = 150.0, huevos = 1200)

        assertEquals(1.5, resultado, 0.0001)
    }

    @Test
    fun conversionKgPorDocena_ceroHuevos_devuelveCero() {
        // Ejemplo: 100.0 kg consumidos con 0 huevos producidos -> evita división por cero -> 0.0.
        val resultado = conversionKgPorDocena(consumoKg = 100.0, huevos = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun conversionKgPorDocena_consumoCero_devuelveCero() {
        // Ejemplo: 0.0 kg consumidos con 600 huevos -> 0.0.
        val resultado = conversionKgPorDocena(consumoKg = 0.0, huevos = 600)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun conversionKgPorKgHuevo_casoNormal_calculaConversion() {
        // Ejemplo: 150.0 kg de alimento, 2,000 huevos producidos, peso promedio por huevo = 60.0 g.
        // Masa total de huevos = (2,000 * 60.0) / 1000 = 120.0 kg de huevo.
        // Conversión = 150.0 kg alimento / 120.0 kg huevo = 1.25 kg alimento/kg huevo.
        val resultado = conversionKgPorKgHuevo(
            consumoKg = 150.0,
            huevos = 2000,
            pesoPromedioHuevoG = 60.0
        )

        assertEquals(1.25, resultado, 0.0001)
    }

    @Test
    fun conversionKgPorKgHuevo_ceroHuevosOPesoCero_devuelveCero() {
        // Cero huevos o peso promedio 0.0 g deben devolver 0.0 sin excepción.
        assertEquals(0.0, conversionKgPorKgHuevo(150.0, 0, 60.0), 0.0)
        assertEquals(0.0, conversionKgPorKgHuevo(150.0, 1000, 0.0), 0.0)
    }

    @Test
    fun conversionKgPorKgHuevo_consumoCero_devuelveCero() {
        // 0.0 kg de alimento consumido con 1,000 huevos a 62.0 g -> 0.0.
        val resultado = conversionKgPorKgHuevo(0.0, 1000, 62.0)

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 10. costoAlimentoSemanal
    // =========================================================================

    @Test
    fun costoAlimentoSemanal_casoNormal_multiplicaYRedondeaAPesos() {
        // Ejemplo: 770.0 kg consumidos en la semana a costo promedio de $1,850.50/kg.
        // 770.0 * 1850.50 = 1,424,885.0 -> redondeado a Long = 1424885L.
        val resultado = costoAlimentoSemanal(
            consumoKgSemana = 770.0,
            costoPromedioPorKg = 1850.50
        )

        assertEquals(1424885L, resultado)
    }

    @Test
    fun costoAlimentoSemanal_redondeoHaciaArribaYAbajo_redondeaCorrectamente() {
        // Caso redondeo hacia abajo: 10.5 kg a $100.20 = 1052.10 -> 1052L
        assertEquals(1052L, costoAlimentoSemanal(10.5, 100.20))

        // Caso redondeo hacia abajo: 10.5 kg a $100.60 = 1056.30 -> 1056L
        assertEquals(1056L, costoAlimentoSemanal(10.5, 100.60))

        // Caso redondeo hacia arriba: 10.0 kg a $100.06 = 1000.6 -> 1001L
        assertEquals(1001L, costoAlimentoSemanal(10.0, 100.06))
    }

    @Test
    fun costoAlimentoSemanal_consumoOCostoCero_devuelveCero() {
        // Consumo 0 o costo 0 devuelve 0L.
        assertEquals(0L, costoAlimentoSemanal(0.0, 1850.0))
        assertEquals(0L, costoAlimentoSemanal(500.0, 0.0))
    }

    // =========================================================================
    // 11. costoPorHuevo y costoPorDocena
    // =========================================================================

    @Test
    fun costoPorHuevo_casoNormal_calculaCostoUnitario() {
        // Ejemplo: Costo total de $350,000 para 1,000 huevos producidos.
        // Costo por huevo = 350,000 / 1000 = $350.0.
        val resultado = costoPorHuevo(costoTotal = 350000L, huevos = 1000)

        assertEquals(350.0, resultado, 0.0001)
    }

    @Test
    fun costoPorHuevo_ceroHuevos_devuelveCero() {
        // Costo total $350,000 con 0 huevos producidos -> 0.0.
        val resultado = costoPorHuevo(costoTotal = 350000L, huevos = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun costoPorHuevo_costoCero_devuelveCero() {
        // Costo $0 con 1,000 huevos -> 0.0.
        val resultado = costoPorHuevo(costoTotal = 0L, huevos = 1000)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun costoPorDocena_casoNormal_calculaCostoDocena() {
        // Ejemplo: Costo total de $360,000 para 1,200 huevos (100 docenas).
        // Costo por docena = 360,000 / 100 = $3,600.0.
        val resultado = costoPorDocena(costoTotal = 360000L, huevos = 1200)

        assertEquals(3600.0, resultado, 0.0001)
    }

    @Test
    fun costoPorDocena_ceroHuevos_devuelveCero() {
        // Cero huevos -> evita división por cero -> 0.0.
        val resultado = costoPorDocena(costoTotal = 360000L, huevos = 0)

        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun costoPorDocena_costoCero_devuelveCero() {
        // Costo $0 con 2,400 huevos -> 0.0.
        val resultado = costoPorDocena(costoTotal = 0L, huevos = 2400)

        assertEquals(0.0, resultado, 0.0)
    }

    // =========================================================================
    // 12. calcularInventarioAlimento (Costo Promedio Ponderado - CPP)
    // =========================================================================

    @Test
    fun calcularInventarioAlimento_entradaYSalidaSimple_mantieneCostoPromedio() {
        // Ejemplo:
        // 1. ENTRADA: 1,000 kg a valor total $2,000,000 ($2,000/kg).
        //    Stock: 1,000 kg, Valor: $2,000,000, Costo Promedio: $2,000/kg.
        // 2. SALIDA: 300 kg (descuenta al costo vigente de $2,000/kg = $600,000).
        //    Stock: 700 kg, Valor: $1,400,000, Costo Promedio: $2,000/kg.
        val movimientos = listOf(
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 1000.0, valorTotal = 2000000L),
            MovimientoAlimento(tipo = TipoMovimiento.SALIDA, kg = 300.0)
        )

        val estado = calcularInventarioAlimento(movimientos)

        assertEquals(700.0, estado.stockKg, 0.0001)
        assertEquals(1400000.0, estado.valorStock, 0.0001)
        assertEquals(2000.0, estado.costoPromedioPorKg, 0.0001)
    }

    @Test
    fun calcularInventarioAlimento_multiplesEntradasPreciosDistintos_recalculaPromedioPonderado() {
        // Ejemplo de ponderación:
        // 1. ENTRADA 1: 1,000 kg a valor $2,000,000 ($2,000/kg).
        // 2. ENTRADA 2: 500 kg a valor $1,300,000 ($2,600/kg).
        //    Stock acumulado: 1,500 kg. Valor acumulado: $3,300,000.
        //    Costo promedio ponderado = 3,300,000 / 1500 = $2,200/kg.
        // 3. SALIDA: 500 kg al costo vigente de $2,200/kg = $1,100,000.
        //    Stock final: 1,000 kg.
        //    Valor final: $2,200,000.
        //    Costo promedio vigente: $2,200/kg.
        val movimientos = listOf(
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 1000.0, valorTotal = 2000000L),
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 500.0, valorTotal = 1300000L),
            MovimientoAlimento(tipo = TipoMovimiento.SALIDA, kg = 500.0)
        )

        val estado = calcularInventarioAlimento(movimientos)

        assertEquals(1000.0, estado.stockKg, 0.0001)
        assertEquals(2200000.0, estado.valorStock, 0.0001)
        assertEquals(2200.0, estado.costoPromedioPorKg, 0.0001)
    }

    @Test
    fun calcularInventarioAlimento_stockLlegaACero_costoPromedioQuedaEnCero() {
        // Ejemplo:
        // 1. ENTRADA: 100 kg a valor $150,000 ($1,500/kg).
        // 2. SALIDA: 100 kg (se consume todo el stock).
        // Regla: si el stock llega a cero, el costo promedio queda en cero.
        val movimientos = listOf(
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 100.0, valorTotal = 150000L),
            MovimientoAlimento(tipo = TipoMovimiento.SALIDA, kg = 100.0)
        )

        val estado = calcularInventarioAlimento(movimientos)

        assertEquals(0.0, estado.stockKg, 0.0)
        assertEquals(0.0, estado.valorStock, 0.0)
        assertEquals(0.0, estado.costoPromedioPorKg, 0.0)
    }

    @Test
    fun calcularInventarioAlimento_listaVacia_devuelveCeros() {
        // Sin movimientos debe devolver stock 0, valor 0 y costo promedio 0.
        val estado = calcularInventarioAlimento(emptyList())

        assertEquals(0.0, estado.stockKg, 0.0)
        assertEquals(0.0, estado.valorStock, 0.0)
        assertEquals(0.0, estado.costoPromedioPorKg, 0.0)
    }

    @Test
    fun calcularInventarioAlimento_reabastecimientoTrasAgotarse_iniciaNuevoCosto() {
        // Ejemplo:
        // 1. ENTRADA: 100 kg a $150,000 ($1,500/kg)
        // 2. SALIDA: 100 kg -> Stock llega a 0 (costo promedio 0)
        // 3. ENTRADA nueva: 200 kg a $360,000 ($1,800/kg)
        //    Stock final = 200 kg, Valor = $360,000, Costo promedio = $1,800/kg.
        val movimientos = listOf(
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 100.0, valorTotal = 150000L),
            MovimientoAlimento(tipo = TipoMovimiento.SALIDA, kg = 100.0),
            MovimientoAlimento(tipo = TipoMovimiento.ENTRADA, kg = 200.0, valorTotal = 360000L)
        )

        val estado = calcularInventarioAlimento(movimientos)

        assertEquals(200.0, estado.stockKg, 0.0001)
        assertEquals(360000.0, estado.valorStock, 0.0001)
        assertEquals(1800.0, estado.costoPromedioPorKg, 0.0001)
    }
}
