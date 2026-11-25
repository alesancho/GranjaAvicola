package com.example.granjaavicola.domain.estandares

import com.example.granjaavicola.data.entity.Estandar
import com.example.granjaavicola.data.entity.FaseEstandar

private const val COLUMNAS_ESPERADAS = 7

/**
 * Parsea el contenido de texto de un CSV de estándares genéticos a una lista de [Estandar].
 *
 * Columnas esperadas:
 * `linea,semana_edad,fase,peso_ave_g,consumo_g_ave_dia,postura_pct,peso_huevo_g`
 *
 * - Soporta saltos de línea `\r\n`, `\n` y `\r`.
 * - Ignora el encabezado y las líneas en blanco.
 * - Los campos numéricos opcionales vacíos se interpretan como `null`.
 * - Si una fila es inválida (cantidad de columnas incorrecta, valores requeridos vacíos o
 *   datos numéricos/fase inválidos), la omite sin lanzar excepción.
 */
fun parsearEstandaresCsv(contenidoCsv: String): List<Estandar> {
    if (contenidoCsv.isBlank()) return emptyList()

    val textoSinBom = contenidoCsv.removePrefix("\uFEFF")
    val resultado = mutableListOf<Estandar>()

    for (lineaCruda in textoSinBom.lineSequence()) {
        val lineaTexto = lineaCruda.trim()
        if (lineaTexto.isEmpty()) continue

        val columnas = lineaTexto.split(",")
        if (columnas.size != COLUMNAS_ESPERADAS) continue

        if (esEncabezado(columnas)) continue

        val estandar = parsearFila(columnas) ?: continue
        resultado.add(estandar)
    }

    return resultado
}

private fun esEncabezado(columnas: List<String>): Boolean {
    return columnas[0].trim().equals("linea", ignoreCase = true) &&
        columnas[1].trim().equals("semana_edad", ignoreCase = true)
}

private fun parsearFila(columnas: List<String>): Estandar? {
    val linea = columnas[0].trim()
    if (linea.isEmpty()) return null

    val semanaEdad = columnas[1].trim().toIntOrNull() ?: return null
    if (semanaEdad <= 0) return null

    val faseTexto = columnas[2].trim().uppercase()
    val fase = runCatching { FaseEstandar.valueOf(faseTexto) }.getOrNull() ?: return null

    val pesoAveG = when (val res = parsearDoubleOpcional(columnas[3])) {
        is ResultadoCampo.Valido -> res.valor
        ResultadoCampo.Invalido -> return null
    }
    val consumoGAveDia = when (val res = parsearDoubleOpcional(columnas[4])) {
        is ResultadoCampo.Valido -> res.valor
        ResultadoCampo.Invalido -> return null
    }
    val posturaPct = when (val res = parsearDoubleOpcional(columnas[5])) {
        is ResultadoCampo.Valido -> res.valor
        ResultadoCampo.Invalido -> return null
    }
    val pesoHuevoG = when (val res = parsearDoubleOpcional(columnas[6])) {
        is ResultadoCampo.Valido -> res.valor
        ResultadoCampo.Invalido -> return null
    }

    return Estandar(
        linea = linea,
        semanaEdad = semanaEdad,
        fase = fase,
        pesoAveG = pesoAveG,
        consumoGAveDia = consumoGAveDia,
        posturaPct = posturaPct,
        pesoHuevoG = pesoHuevoG
    )
}

private sealed interface ResultadoCampo {
    data class Valido(val valor: Double?) : ResultadoCampo
    data object Invalido : ResultadoCampo
}

private fun parsearDoubleOpcional(raw: String): ResultadoCampo {
    val limpio = raw.trim()
    if (limpio.isEmpty()) return ResultadoCampo.Valido(null)
    val valor = limpio.toDoubleOrNull() ?: return ResultadoCampo.Invalido
    if (!valor.isFinite() || valor < 0.0) return ResultadoCampo.Invalido
    return ResultadoCampo.Valido(valor)
}
