package com.example.granjaavicola.domain.calculos

import com.example.granjaavicola.data.entity.RegistroDiario
import java.time.LocalDate

data class ResultadoSaldoRegistro(
    val avesVivasAntesDeFecha: Int,
    val avesVivasConDigitado: Int
)

/**
 * Calcula las aves vivas antes de las bajas de [fechaEditada] y el saldo resultante
 * incluyendo [mortalidadDigitada] y [descarteDigitado] para esa fecha.
 * avesVendidas = 0 por ahora.
 */
fun calcularSaldoRegistro(
    avesIniciales: Int,
    registrosDelLote: List<RegistroDiario>,
    fechaEditada: LocalDate,
    mortalidadDigitada: Int,
    descarteDigitado: Int
): ResultadoSaldoRegistro {
    val otrosRegistros = registrosDelLote.filter { it.fecha != fechaEditada }
    val mortalidadOtros = otrosRegistros.sumOf { it.mortalidad }
    val descarteOtros = otrosRegistros.sumOf { it.descarte }

    val antesDeFecha = saldoAves(
        avesIniciales = avesIniciales,
        mortalidadAcumulada = mortalidadOtros,
        descarteAcumulado = descarteOtros,
        avesVendidas = 0
    )
    val conDigitado = saldoAves(
        avesIniciales = avesIniciales,
        mortalidadAcumulada = mortalidadOtros + mortalidadDigitada.coerceAtLeast(0),
        descarteAcumulado = descarteOtros + descarteDigitado.coerceAtLeast(0),
        avesVendidas = 0
    )

    return ResultadoSaldoRegistro(
        avesVivasAntesDeFecha = antesDeFecha,
        avesVivasConDigitado = conDigitado
    )
}

fun validarFechaRegistro(
    fecha: LocalDate,
    fechaLlegadaLote: LocalDate,
    hoy: LocalDate = LocalDate.now()
): String? = when {
    fecha.isAfter(hoy) -> "La fecha no puede ser futura"
    fecha.isBefore(fechaLlegadaLote) -> "La fecha no puede ser anterior a la llegada del lote"
    else -> null
}

fun validarFechaTraslado(
    fechaTraslado: LocalDate,
    fechaLlegadaLote: LocalDate,
    hoy: LocalDate = LocalDate.now()
): String? = when {
    fechaTraslado.isAfter(hoy) -> "La fecha de traslado no puede ser futura"
    fechaTraslado.isBefore(fechaLlegadaLote) -> "La fecha de traslado no puede ser anterior a la fecha de llegada"
    else -> null
}

data class EntradaFormularioRegistro(
    val hayLoteSeleccionado: Boolean,
    val fecha: LocalDate,
    val fechaLlegadaLote: LocalDate?,
    val esProduccion: Boolean,
    val avesVivasAntesDeFecha: Int,
    val consumoKg: String,
    val mortalidad: String,
    val descarte: String,
    val aguaLitros: String,
    val temperaturaMax: String,
    val temperaturaMin: String,
    val horasLuz: String,
    val observaciones: String,
    val huevosAA: String,
    val huevosA: String,
    val huevosB: String,
    val huevosC: String,
    val huevosRotos: String,
    val huevosSucios: String
)

data class DatosRegistroValidados(
    val consumoKg: Double,
    val mortalidad: Int,
    val descarte: Int,
    val aguaLitros: Double,
    val temperaturaMax: Double,
    val temperaturaMin: Double,
    val horasLuz: Double,
    val observaciones: String,
    val huevosAA: Int,
    val huevosA: Int,
    val huevosB: Int,
    val huevosC: Int,
    val huevosRotos: Int,
    val huevosSucios: Int
)

data class ErroresFormularioRegistro(
    val errorLote: String? = null,
    val errorFecha: String? = null,
    val errorConsumoKg: String? = null,
    val errorMortalidad: String? = null,
    val errorDescarte: String? = null,
    val errorAguaLitros: String? = null,
    val errorTemperaturaMax: String? = null,
    val errorTemperaturaMin: String? = null,
    val errorHorasLuz: String? = null,
    val errorHuevosAA: String? = null,
    val errorHuevosA: String? = null,
    val errorHuevosB: String? = null,
    val errorHuevosC: String? = null,
    val errorHuevosRotos: String? = null,
    val errorHuevosSucios: String? = null
) {
    val tieneErrores: Boolean
        get() = listOf(
            errorLote,
            errorFecha,
            errorConsumoKg,
            errorMortalidad,
            errorDescarte,
            errorAguaLitros,
            errorTemperaturaMax,
            errorTemperaturaMin,
            errorHorasLuz,
            errorHuevosAA,
            errorHuevosA,
            errorHuevosB,
            errorHuevosC,
            errorHuevosRotos,
            errorHuevosSucios
        ).any { it != null }
}

data class ResultadoValidacionRegistro(
    val errores: ErroresFormularioRegistro,
    val datos: DatosRegistroValidados?
)

fun parsearDecimalNoNegativo(texto: String): Double? {
    val limpio = texto.trim().replace(',', '.')
    if (limpio.isEmpty()) return null
    val valor = limpio.toDoubleOrNull() ?: return null
    return if (valor.isNaN() || valor.isInfinite()) null else valor
}

private fun validarDecimalRequerido(texto: String, mensajeVacio: String): Pair<Double?, String?> {
    val limpio = texto.trim()
    if (limpio.isEmpty()) return null to mensajeVacio
    val valor = parsearDecimalNoNegativo(limpio) ?: return null to "Valor inválido"
    if (valor < 0.0) return null to "No se permiten valores negativos"
    return valor to null
}

private fun validarDecimalOpcional(texto: String): Pair<Double?, String?> {
    val limpio = texto.trim()
    if (limpio.isEmpty()) return 0.0 to null
    val valor = parsearDecimalNoNegativo(limpio) ?: return null to "Valor inválido"
    if (valor < 0.0) return null to "No se permiten valores negativos"
    return valor to null
}

private fun validarEnteroOpcional(texto: String): Pair<Int?, String?> {
    val limpio = texto.trim()
    if (limpio.isEmpty()) return 0 to null
    val valor = limpio.toIntOrNull() ?: return null to "Número entero inválido"
    if (valor < 0) return null to "No se permiten valores negativos"
    return valor to null
}

fun validarFormularioRegistro(
    entrada: EntradaFormularioRegistro,
    hoy: LocalDate = LocalDate.now()
): ResultadoValidacionRegistro {
    val errorLote = if (!entrada.hayLoteSeleccionado || entrada.fechaLlegadaLote == null) {
        "Selecciona un lote"
    } else null

    val errorFecha = entrada.fechaLlegadaLote?.let { llegada ->
        validarFechaRegistro(entrada.fecha, llegada, hoy)
    }

    val (consumo, errorConsumo) = validarDecimalRequerido(
        entrada.consumoKg,
        "Ingresa el consumo de alimento"
    )
    var (mortalidad, errorMortalidad) = validarEnteroOpcional(entrada.mortalidad)
    var (descarte, errorDescarte) = validarEnteroOpcional(entrada.descarte)
    val (agua, errorAgua) = validarDecimalOpcional(entrada.aguaLitros)
    val (tempMax, errorTempMax) = validarDecimalOpcional(entrada.temperaturaMax)
    val (tempMin, errorTempMin) = validarDecimalOpcional(entrada.temperaturaMin)
    val (horasLuz, errorHorasLuz) = validarDecimalOpcional(entrada.horasLuz)

    if (mortalidad != null && descarte != null && errorLote == null) {
        if (mortalidad + descarte > entrada.avesVivasAntesDeFecha) {
            val mensaje = "Mortalidad + descarte no puede superar las aves vivas (${entrada.avesVivasAntesDeFecha})"
            if (mortalidad > 0) errorMortalidad = mensaje
            if (descarte > 0) errorDescarte = mensaje
            if (mortalidad == 0 && descarte == 0) errorMortalidad = mensaje
        }
    }

    val aaRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosAA) else (0 to null)
    val aRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosA) else (0 to null)
    val bRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosB) else (0 to null)
    val cRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosC) else (0 to null)
    val rotosRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosRotos) else (0 to null)
    val suciosRes = if (entrada.esProduccion) validarEnteroOpcional(entrada.huevosSucios) else (0 to null)

    val errores = ErroresFormularioRegistro(
        errorLote = errorLote,
        errorFecha = errorFecha,
        errorConsumoKg = errorConsumo,
        errorMortalidad = errorMortalidad,
        errorDescarte = errorDescarte,
        errorAguaLitros = errorAgua,
        errorTemperaturaMax = errorTempMax,
        errorTemperaturaMin = errorTempMin,
        errorHorasLuz = errorHorasLuz,
        errorHuevosAA = aaRes.second,
        errorHuevosA = aRes.second,
        errorHuevosB = bRes.second,
        errorHuevosC = cRes.second,
        errorHuevosRotos = rotosRes.second,
        errorHuevosSucios = suciosRes.second
    )

    val datos = if (!errores.tieneErrores) {
        DatosRegistroValidados(
            consumoKg = consumo!!,
            mortalidad = mortalidad!!,
            descarte = descarte!!,
            aguaLitros = agua!!,
            temperaturaMax = tempMax!!,
            temperaturaMin = tempMin!!,
            horasLuz = horasLuz!!,
            observaciones = entrada.observaciones.trim(),
            huevosAA = aaRes.first!!,
            huevosA = aRes.first!!,
            huevosB = bRes.first!!,
            huevosC = cRes.first!!,
            huevosRotos = rotosRes.first!!,
            huevosSucios = suciosRes.first!!
        )
    } else null

    return ResultadoValidacionRegistro(errores = errores, datos = datos)
}
