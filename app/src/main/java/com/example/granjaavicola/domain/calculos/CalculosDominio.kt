package com.example.granjaavicola.domain.calculos

import com.example.granjaavicola.domain.modelos.EstadoInventario
import com.example.granjaavicola.domain.modelos.MovimientoAlimento
import com.example.granjaavicola.domain.modelos.TipoMovimiento
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * 1. Calcula la edad en semanas de un lote de aves.
 * Semana 1 = días 0 a 6 desde el nacimiento.
 * Si la fecha consultada es anterior al nacimiento, devuelve 0.
 *
 * @param fechaNacimiento Fecha de nacimiento del lote.
 * @param fecha Fecha de consulta o registro.
 * @return Número de semana (1, 2, ...), o 0 si la fecha es anterior al nacimiento.
 */
fun edadEnSemanas(fechaNacimiento: LocalDate, fecha: LocalDate): Int {
    if (fecha.isBefore(fechaNacimiento)) return 0
    val dias = ChronoUnit.DAYS.between(fechaNacimiento, fecha)
    if (dias < 0L) return 0
    return (dias / 7).toInt() + 1
}

/**
 * 2. Calcula el saldo actual de aves vivas en el lote.
 * saldo = avesIniciales - mortalidadAcumulada - descarteAcumulado - avesVendidas.
 * Nunca devuelve un número negativo (mínimo 0).
 *
 * @param avesIniciales Cantidad inicial de aves alojadas.
 * @param mortalidadAcumulada Total acumulado de aves muertas.
 * @param descarteAcumulado Total acumulado de aves descartadas.
 * @param avesVendidas Total acumulado de aves vendidas.
 * @return Saldo de aves vivas (>= 0).
 */
fun saldoAves(
    avesIniciales: Int,
    mortalidadAcumulada: Int,
    descarteAcumulado: Int,
    avesVendidas: Int
): Int {
    val saldo = avesIniciales - mortalidadAcumulada - descarteAcumulado - avesVendidas
    return maxOf(0, saldo)
}

/**
 * 3. Calcula el porcentaje de mortalidad semanal.
 * (muertesSemana / saldoInicioSemana) * 100.
 * Si el saldo inicial es <= 0 o muertes <= 0, devuelve 0.0.
 *
 * @param muertesSemana Bajas por muerte durante la semana.
 * @param saldoInicioSemana Cantidad de aves vivas al inicio de la semana.
 * @return Porcentaje de mortalidad semanal (0.0 a 100.0+).
 */
fun porcentajeMortalidadSemanal(muertesSemana: Int, saldoInicioSemana: Int): Double {
    if (saldoInicioSemana <= 0 || muertesSemana <= 0) return 0.0
    return (muertesSemana.toDouble() / saldoInicioSemana) * 100.0
}

/**
 * 4. Calcula el peso promedio de una muestra de aves o huevos.
 * Devuelve 0.0 si la lista está vacía.
 *
 * @param pesos Lista de pesos registrados.
 * @return Promedio aritmético de los pesos.
 */
fun pesoPromedio(pesos: List<Double>): Double {
    if (pesos.isEmpty()) return 0.0
    return pesos.sum() / pesos.size
}

/**
 * 5. Calcula el porcentaje de uniformidad de una muestra de pesos.
 * Es el porcentaje de aves cuyo peso se encuentra dentro de ±10% del promedio de la muestra.
 * Devuelve 0.0 si la lista está vacía o el promedio es <= 0.0.
 *
 * @param pesos Lista de pesos de las aves pesadas.
 * @return Porcentaje de aves en el rango de uniformidad (0.0 a 100.0).
 */
fun uniformidadPct(pesos: List<Double>): Double {
    if (pesos.isEmpty()) return 0.0
    val promedio = pesoPromedio(pesos)
    if (promedio <= 0.0) return 0.0
    val limiteInferior = promedio * 0.9
    val limiteSuperior = promedio * 1.1
    val dentroDeRango = pesos.count { it in limiteInferior..limiteSuperior }
    return (dentroDeRango.toDouble() / pesos.size) * 100.0
}

/**
 * 6. Calcula el coeficiente de variación porcentual (%CV).
 * Desviación estándar muestral (con divisor n - 1) dividida por el promedio de la muestra, multiplicada por 100.
 * Si la muestra tiene menos de 2 datos o el promedio es <= 0.0, devuelve 0.0.
 *
 * @param pesos Lista de pesos de la muestra.
 * @return Coeficiente de variación porcentual (%CV).
 */
fun coeficienteVariacionPct(pesos: List<Double>): Double {
    if (pesos.size < 2) return 0.0
    val promedio = pesoPromedio(pesos)
    if (promedio <= 0.0) return 0.0
    val sumaDiferenciasCuadradas = pesos.sumOf { (it - promedio).pow(2) }
    val varianzaMuestral = sumaDiferenciasCuadradas / (pesos.size - 1)
    val desviacionEstandar = sqrt(varianzaMuestral)
    return (desviacionEstandar / promedio) * 100.0
}

/**
 * 7. Calcula el consumo diario de alimento en gramos por ave viva.
 * (consumoKg * 1000) / avesVivas.
 * Si avesVivas <= 0 o consumoKg <= 0.0, devuelve 0.0.
 *
 * @param consumoKg Kilogramos de alimento consumidos en el día.
 * @param avesVivas Cantidad de aves vivas al momento del consumo.
 * @return Gramos consumidos por ave al día.
 */
fun consumoGramosPorAveDia(consumoKg: Double, avesVivas: Int): Double {
    if (avesVivas <= 0 || consumoKg <= 0.0) return 0.0
    return (consumoKg * 1000.0) / avesVivas
}

/**
 * 8. Calcula el porcentaje de postura diario (% postura = (huevosDia / avesVivas) * 100).
 * Si avesVivas <= 0 o huevosDia <= 0, devuelve 0.0.
 *
 * @param huevosDia Cantidad total de huevos recolectados en el día.
 * @param avesVivas Cantidad de aves vivas en el lote.
 * @return Porcentaje de postura (0.0 a 100.0+).
 */
fun porcentajePostura(huevosDia: Int, avesVivas: Int): Double {
    if (avesVivas <= 0 || huevosDia <= 0) return 0.0
    return (huevosDia.toDouble() / avesVivas) * 100.0
}

/**
 * 9a. Calcula la conversión alimenticia en kilogramos de alimento por docena de huevos producida.
 * consumoKg / (huevos / 12) = (consumoKg * 12) / huevos.
 * Si huevos <= 0 o consumoKg <= 0.0, devuelve 0.0.
 *
 * @param consumoKg Kilogramos de alimento consumidos en el período.
 * @param huevos Cantidad total de huevos producidos en el período.
 * @return Kg de alimento por docena de huevos.
 */
fun conversionKgPorDocena(consumoKg: Double, huevos: Int): Double {
    if (huevos <= 0 || consumoKg <= 0.0) return 0.0
    val docenas = huevos.toDouble() / 12.0
    return consumoKg / docenas
}

/**
 * 9b. Calcula la conversión alimenticia en kilogramos de alimento por kilogramo de huevo producido.
 * kgHuevo = (huevos * pesoPromedioHuevoG) / 1000.
 * conversion = consumoKg / kgHuevo.
 * Si huevos <= 0, pesoPromedioHuevoG <= 0.0 o consumoKg <= 0.0, devuelve 0.0.
 *
 * @param consumoKg Kilogramos de alimento consumidos en el período.
 * @param huevos Cantidad total de huevos producidos en el período.
 * @param pesoPromedioHuevoG Peso promedio de un huevo en gramos.
 * @return Conversión alimenticia (Kg alimento / Kg huevo).
 */
fun conversionKgPorKgHuevo(consumoKg: Double, huevos: Int, pesoPromedioHuevoG: Double): Double {
    if (huevos <= 0 || pesoPromedioHuevoG <= 0.0 || consumoKg <= 0.0) return 0.0
    val kgHuevo = (huevos * pesoPromedioHuevoG) / 1000.0
    return consumoKg / kgHuevo
}

/**
 * 10. Calcula el costo del alimento semanal redondeado a pesos enteros (Long).
 * (consumoKgSemana * costoPromedioPorKg).roundToLong().
 * Si el consumo o costo son <= 0.0, devuelve 0L.
 *
 * @param consumoKgSemana Kilogramos consumidos en la semana.
 * @param costoPromedioPorKg Costo unitario promedio ponderado por kilogramo de alimento.
 * @return Costo total semanal redondeado a pesos.
 */
fun costoAlimentoSemanal(consumoKgSemana: Double, costoPromedioPorKg: Double): Long {
    if (consumoKgSemana <= 0.0 || costoPromedioPorKg <= 0.0) return 0L
    return (consumoKgSemana * costoPromedioPorKg).roundToLong()
}

/**
 * 11a. Calcula el costo de producción por huevo individual.
 * costoTotal / huevos.
 * Si huevos <= 0 o costoTotal <= 0L, devuelve 0.0.
 *
 * @param costoTotal Costo monetario total (en pesos).
 * @param huevos Cantidad total de huevos producidos.
 * @return Costo por huevo individual.
 */
fun costoPorHuevo(costoTotal: Long, huevos: Int): Double {
    if (huevos <= 0 || costoTotal <= 0L) return 0.0
    return costoTotal.toDouble() / huevos
}

/**
 * 11b. Calcula el costo de producción por docena de huevos.
 * costoTotal / (huevos / 12) = (costoTotal * 12) / huevos.
 * Si huevos <= 0 o costoTotal <= 0L, devuelve 0.0.
 *
 * @param costoTotal Costo monetario total (en pesos).
 * @param huevos Cantidad total de huevos producidos.
 * @return Costo por docena de huevos.
 */
fun costoPorDocena(costoTotal: Long, huevos: Int): Double {
    if (huevos <= 0 || costoTotal <= 0L) return 0.0
    val docenas = huevos.toDouble() / 12.0
    return costoTotal.toDouble() / docenas
}

/**
 * 12. Calcula el estado del inventario de alimento con costo promedio ponderado (CPP)
 * a partir de una lista de movimientos cronológicos.
 *
 * - Las entradas aumentan los kilogramos y el valor monetario en stock, recalculando el costo promedio.
 * - Las salidas descuentan al costo promedio ponderado vigente al momento de la salida.
 * - Si el stock llega a cero (o menor), el stock, valor del stock y costo promedio quedan en cero.
 * - Si la lista de movimientos está vacía, devuelve stock 0, valor 0 y costo promedio 0.
 *
 * @param movimientos Lista de movimientos de inventario en orden cronológico.
 * @return [EstadoInventario] con stockKg, valorStock y costoPromedioPorKg.
 */
fun calcularInventarioAlimento(movimientos: List<MovimientoAlimento>): EstadoInventario {
    if (movimientos.isEmpty()) {
        return EstadoInventario(stockKg = 0.0, valorStock = 0.0, costoPromedioPorKg = 0.0)
    }

    var stockKg = 0.0
    var valorStock = 0.0
    var costoPromedioPorKg = 0.0

    for (mov in movimientos) {
        if (mov.kg <= 0.0) continue

        when (mov.tipo) {
            TipoMovimiento.ENTRADA -> {
                stockKg += mov.kg
                valorStock += mov.valorTotal.toDouble()
                costoPromedioPorKg = if (stockKg > 0.0) valorStock / stockKg else 0.0
            }
            TipoMovimiento.SALIDA -> {
                if (stockKg <= 0.0 || mov.kg >= stockKg) {
                    stockKg = 0.0
                    valorStock = 0.0
                    costoPromedioPorKg = 0.0
                } else {
                    stockKg -= mov.kg
                    valorStock = stockKg * costoPromedioPorKg
                }
            }
        }
    }

    return EstadoInventario(
        stockKg = stockKg,
        valorStock = valorStock,
        costoPromedioPorKg = costoPromedioPorKg
    )
}

/**
 * Alias de [calcularInventarioAlimento] para compatibilidad de nomenclatura.
 */
fun calcularCostoPromedioPonderado(movimientos: List<MovimientoAlimento>): EstadoInventario =
    calcularInventarioAlimento(movimientos)

/**
 * Alias de [calcularInventarioAlimento] para compatibilidad de nomenclatura.
 */
fun calcularEstadoInventario(movimientos: List<MovimientoAlimento>): EstadoInventario =
    calcularInventarioAlimento(movimientos)
