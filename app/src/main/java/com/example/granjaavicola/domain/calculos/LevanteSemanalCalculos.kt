package com.example.granjaavicola.domain.calculos

import com.example.granjaavicola.data.entity.Estandar
import com.example.granjaavicola.data.entity.RegistroDiario
import java.time.LocalDate
import kotlin.math.roundToLong

data class FilaSemanaLevante(
    val semana: Int,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val diasRegistrados: Int,
    val avesInicioSemana: Int,
    val avesFinSemana: Int,
    val mortalidadSemana: Int,
    val descarteSemana: Int,
    val porcentajeMortalidad: Double,
    val consumoTotalKg: Double,
    val consumoPromedioDiarioKg: Double,
    val consumoGramosAveDiaReal: Double,
    val consumoGramosAveDiaEstandar: Double?,
    val pesoPromedioEstandarG: Double?,
    val costoAlimentoSemanalCop: Long,
    val alertas: List<String>
)

fun calcularResumenSemanalLevante(
    fechaNacimiento: LocalDate,
    avesIniciales: Int,
    registros: List<RegistroDiario>,
    estandaresLevante: List<Estandar>,
    costoPromedioPorKgAlimento: Double,
    semanaMaxima: Int = 18
): List<FilaSemanaLevante> {
    if (avesIniciales <= 0 || semanaMaxima < 1) return emptyList()

    val estandarPorSemana = estandaresLevante.associateBy { it.semanaEdad }

    val registrosPorSemana: Map<Int, List<RegistroDiario>> = registros
        .mapNotNull { reg ->
            val sem = edadEnSemanas(fechaNacimiento, reg.fecha)
            if (sem in 1..semanaMaxima) sem to reg else null
        }
        .groupBy({ it.first }, { it.second })

    var mortalidadAcumuladaPrevia = 0
    var descarteAcumuladoPrevia = 0
    val filas = mutableListOf<FilaSemanaLevante>()

    for (semana in 1..semanaMaxima) {
        val inicioSemana = fechaNacimiento.plusDays(((semana - 1) * 7L))
        val finSemana = inicioSemana.plusDays(6L)

        val avesInicio = saldoAves(
            avesIniciales = avesIniciales,
            mortalidadAcumulada = mortalidadAcumuladaPrevia,
            descarteAcumulado = descarteAcumuladoPrevia,
            avesVendidas = 0
        )

        val regsSemana = registrosPorSemana[semana].orEmpty()
        val mortSemana = regsSemana.sumOf { it.mortalidad }
        val descSemana = regsSemana.sumOf { it.descarte }
        val consumoSemanaKg = regsSemana.sumOf { it.consumoKg }
        val diasConRegistro = regsSemana.size

        mortalidadAcumuladaPrevia += mortSemana
        descarteAcumuladoPrevia += descSemana

        val avesFin = saldoAves(
            avesIniciales = avesIniciales,
            mortalidadAcumulada = mortalidadAcumuladaPrevia,
            descarteAcumulado = descarteAcumuladoPrevia,
            avesVendidas = 0
        )

        val pctMortalidad = porcentajeMortalidadSemanal(
            muertesSemana = mortSemana,
            saldoInicioSemana = avesInicio
        )

        val promedioDiarioKg = if (diasConRegistro > 0) {
            consumoSemanaKg / diasConRegistro
        } else {
            0.0
        }

        val consumoGramosAveDiaReal = if (diasConRegistro > 0 && avesInicio > 0) {
            consumoGramosPorAveDia(
                consumoKg = promedioDiarioKg,
                avesVivas = avesInicio
            )
        } else {
            0.0
        }

        val estandar = estandarPorSemana[semana]
        val consumoEstandar = estandar?.consumoGAveDia
        val pesoEstandar = estandar?.pesoAveG

        val costoSemanal = if (costoPromedioPorKgAlimento > 0.0) {
            costoAlimentoSemanal(
                consumoKgSemana = consumoSemanaKg,
                costoPromedioPorKg = costoPromedioPorKgAlimento
            )
        } else {
            ((consumoSemanaKg * costoPromedioPorKgAlimento).roundToLong()).coerceAtLeast(0L)
        }

        val alertas = mutableListOf<String>()
        if (pctMortalidad >= 1.0) {
            alertas.add("Mortalidad semanal alta (${String.format("%.1f", pctMortalidad)}%)")
        }
        if (consumoEstandar != null && consumoEstandar > 0.0 && diasConRegistro > 0) {
            if (consumoGramosAveDiaReal < consumoEstandar * 0.85) {
                alertas.add("Consumo por debajo del estándar")
            }
        }
        if (avesFin == 0 && avesInicio > 0) {
            alertas.add("Sin aves vivas al cierre de la semana")
        }

        filas.add(
            FilaSemanaLevante(
                semana = semana,
                fechaInicio = inicioSemana,
                fechaFin = finSemana,
                diasRegistrados = diasConRegistro,
                avesInicioSemana = avesInicio,
                avesFinSemana = avesFin,
                mortalidadSemana = mortSemana,
                descarteSemana = descSemana,
                porcentajeMortalidad = pctMortalidad,
                consumoTotalKg = consumoSemanaKg,
                consumoPromedioDiarioKg = promedioDiarioKg,
                consumoGramosAveDiaReal = consumoGramosAveDiaReal,
                consumoGramosAveDiaEstandar = consumoEstandar,
                pesoPromedioEstandarG = pesoEstandar,
                costoAlimentoSemanalCop = costoSemanal,
                alertas = alertas
            )
        )
    }

    return filas
}