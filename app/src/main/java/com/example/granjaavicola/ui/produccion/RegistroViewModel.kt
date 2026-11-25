package com.example.granjaavicola.ui.produccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Lote
import com.example.granjaavicola.data.entity.ProduccionHuevo
import com.example.granjaavicola.data.entity.RegistroDiario
import com.example.granjaavicola.data.repository.LoteRepository
import com.example.granjaavicola.data.repository.ProduccionHuevoRepository
import com.example.granjaavicola.data.repository.RegistroDiarioRepository
import com.example.granjaavicola.domain.calculos.EntradaFormularioRegistro
import com.example.granjaavicola.domain.calculos.calcularSaldoRegistro
import com.example.granjaavicola.domain.calculos.consumoGramosPorAveDia
import com.example.granjaavicola.domain.calculos.parsearDecimalNoNegativo
import com.example.granjaavicola.domain.calculos.porcentajePostura
import com.example.granjaavicola.domain.calculos.validarFechaRegistro
import com.example.granjaavicola.domain.calculos.validarFormularioRegistro
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RegistroUiState(
    val lotesActivos: List<Lote> = emptyList(),
    val cargandoLotes: Boolean = true,
    val loteSeleccionado: Lote? = null,
    val fecha: LocalDate = LocalDate.now(),
    val esEdicion: Boolean = false,
    val registroExistenteId: Long? = null,
    val produccionExistenteId: Long? = null,
    // Campos comunes
    val consumoKg: String = "",
    val mortalidad: String = "",
    val descarte: String = "",
    val aguaLitros: String = "",
    val temperaturaMax: String = "",
    val temperaturaMin: String = "",
    val horasLuz: String = "",
    val observaciones: String = "",
    // Campos de producción (solo EstadoLote.PRODUCCION)
    val huevosAA: String = "",
    val huevosA: String = "",
    val huevosB: String = "",
    val huevosC: String = "",
    val huevosRotos: String = "",
    val huevosSucios: String = "",
    // Métricas en vivo
    val avesVivas: Int = 0,
    val avesVivasAntesDeFecha: Int = 0,
    val consumoGramosAveDia: Double = 0.0,
    val totalHuevos: Int = 0,
    val porcentajePostura: Double = 0.0,
    // Errores
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
    val errorHuevosSucios: String? = null,
    val errorGeneral: String? = null,
    // Estado de guardado
    val guardando: Boolean = false,
    val mensajeSnackbar: String? = null
)

private data class DatosContextoRegistro(
    val lote: Lote?,
    val fecha: LocalDate,
    val registrosDelLote: List<RegistroDiario>,
    val registroDia: RegistroDiario?,
    val produccionDia: ProduccionHuevo?
)

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModel(
    private val loteRepository: LoteRepository,
    private val registroDiarioRepository: RegistroDiarioRepository,
    private val produccionHuevoRepository: ProduccionHuevoRepository,
    private val db: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState

    private val _loteIdSeleccionado = MutableStateFlow<Long?>(null)
    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())

    private var registrosDelLoteActual: List<RegistroDiario> = emptyList()
    private var claveCargada: Pair<Long?, LocalDate>? = null

    init {
        viewModelScope.launch {
            loteRepository.lotesActivos.collect { lotes ->
                val actualId = _loteIdSeleccionado.value
                val nuevoId = when {
                    lotes.isEmpty() -> null
                    lotes.size == 1 -> lotes.first().id
                    actualId != null && lotes.any { it.id == actualId } -> actualId
                    else -> null
                }
                _loteIdSeleccionado.value = nuevoId
                _uiState.value = _uiState.value.copy(
                    lotesActivos = lotes,
                    cargandoLotes = false
                )
            }
        }

        viewModelScope.launch {
            combine(
                _loteIdSeleccionado,
                _fechaSeleccionada,
                loteRepository.lotesActivos
            ) { id, fecha, lotes ->
                lotes.firstOrNull { it.id == id } to fecha
            }.flatMapLatest { (lote, fecha) ->
                if (lote == null) {
                    flowOf(
                        DatosContextoRegistro(
                            lote = null,
                            fecha = fecha,
                            registrosDelLote = emptyList(),
                            registroDia = null,
                            produccionDia = null
                        )
                    )
                } else {
                    combine(
                        registroDiarioRepository.getPorLote(lote.id),
                        registroDiarioRepository.getPorLoteYFecha(lote.id, fecha),
                        produccionHuevoRepository.getPorLoteYFecha(lote.id, fecha)
                    ) { registros, registroDia, produccionDia ->
                        DatosContextoRegistro(
                            lote = lote,
                            fecha = fecha,
                            registrosDelLote = registros,
                            registroDia = registroDia,
                            produccionDia = produccionDia
                        )
                    }
                }
            }.collect { contexto ->
                registrosDelLoteActual = contexto.registrosDelLote
                val nuevaClave = contexto.lote?.id to contexto.fecha

                if (claveCargada != nuevaClave) {
                    claveCargada = nuevaClave
                    cargarFormularioDesdeContexto(contexto)
                } else {
                    val actual = _uiState.value.copy(
                        loteSeleccionado = contexto.lote,
                        fecha = contexto.fecha,
                        esEdicion = contexto.registroDia != null,
                        registroExistenteId = contexto.registroDia?.id,
                        produccionExistenteId = contexto.produccionDia?.id,
                        errorFecha = contexto.lote?.let {
                            validarFechaRegistro(contexto.fecha, it.fechaLlegada)
                        }
                    )
                    _uiState.value = recalcularMetricasEnVivo(actual)
                }
            }
        }
    }

    private fun cargarFormularioDesdeContexto(contexto: DatosContextoRegistro) {
        val reg = contexto.registroDia
        val prod = contexto.produccionDia
        val errorFecha = contexto.lote?.let {
            validarFechaRegistro(contexto.fecha, it.fechaLlegada)
        }

        val nuevoEstado = _uiState.value.copy(
            loteSeleccionado = contexto.lote,
            fecha = contexto.fecha,
            esEdicion = reg != null,
            registroExistenteId = reg?.id,
            produccionExistenteId = prod?.id,
            consumoKg = reg?.let { formatearDecimalCampo(it.consumoKg, vacioSiCero = false) } ?: "",
            mortalidad = reg?.mortalidad?.toString() ?: "",
            descarte = reg?.descarte?.toString() ?: "",
            aguaLitros = reg?.let { formatearDecimalCampo(it.aguaLitros, vacioSiCero = true) } ?: "",
            temperaturaMax = reg?.let { formatearDecimalCampo(it.temperaturaMax, vacioSiCero = true) } ?: "",
            temperaturaMin = reg?.let { formatearDecimalCampo(it.temperaturaMin, vacioSiCero = true) } ?: "",
            horasLuz = reg?.let { formatearDecimalCampo(it.horasLuz, vacioSiCero = true) } ?: "",
            observaciones = reg?.observaciones ?: "",
            huevosAA = prod?.huevosAA?.toString() ?: "",
            huevosA = prod?.huevosA?.toString() ?: "",
            huevosB = prod?.huevosB?.toString() ?: "",
            huevosC = prod?.huevosC?.toString() ?: "",
            huevosRotos = prod?.huevosRotos?.toString() ?: "",
            huevosSucios = prod?.huevosSucios?.toString() ?: "",
            errorLote = null,
            errorFecha = errorFecha,
            errorConsumoKg = null,
            errorMortalidad = null,
            errorDescarte = null,
            errorAguaLitros = null,
            errorTemperaturaMax = null,
            errorTemperaturaMin = null,
            errorHorasLuz = null,
            errorHuevosAA = null,
            errorHuevosA = null,
            errorHuevosB = null,
            errorHuevosC = null,
            errorHuevosRotos = null,
            errorHuevosSucios = null,
            errorGeneral = null
        )
        _uiState.value = recalcularMetricasEnVivo(nuevoEstado)
    }

    private fun recalcularMetricasEnVivo(estado: RegistroUiState): RegistroUiState {
        val lote = estado.loteSeleccionado ?: return estado.copy(
            avesVivas = 0,
            avesVivasAntesDeFecha = 0,
            consumoGramosAveDia = 0.0,
            totalHuevos = 0,
            porcentajePostura = 0.0
        )

        val mortDigitada = estado.mortalidad.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
        val descDigitado = estado.descarte.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
        val saldo = calcularSaldoRegistro(
            avesIniciales = lote.avesIniciales,
            registrosDelLote = registrosDelLoteActual,
            fechaEditada = estado.fecha,
            mortalidadDigitada = mortDigitada,
            descarteDigitado = descDigitado
        )

        val consumo = parsearDecimalNoNegativo(estado.consumoKg)?.coerceAtLeast(0.0) ?: 0.0
        val gramosAveDia = consumoGramosPorAveDia(consumo, saldo.avesVivasConDigitado)

        val totalHuevos = if (lote.estado == EstadoLote.PRODUCCION) {
            (estado.huevosAA.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0) +
                (estado.huevosA.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0) +
                (estado.huevosB.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0) +
                (estado.huevosC.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0) +
                (estado.huevosRotos.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0) +
                (estado.huevosSucios.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0)
        } else {
            0
        }
        val postura = if (lote.estado == EstadoLote.PRODUCCION) {
            porcentajePostura(totalHuevos, saldo.avesVivasConDigitado)
        } else {
            0.0
        }

        return estado.copy(
            avesVivas = saldo.avesVivasConDigitado,
            avesVivasAntesDeFecha = saldo.avesVivasAntesDeFecha,
            consumoGramosAveDia = gramosAveDia,
            totalHuevos = totalHuevos,
            porcentajePostura = postura
        )
    }

    fun seleccionarLote(loteId: Long) {
        _loteIdSeleccionado.value = loteId
    }

    fun seleccionarFecha(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun seleccionarHoy() {
        _fechaSeleccionada.value = LocalDate.now()
    }

    fun seleccionarAyer() {
        _fechaSeleccionada.value = LocalDate.now().minusDays(1)
    }

    fun actualizarConsumoKg(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(consumoKg = valor, errorConsumoKg = null, errorGeneral = null)
        )
    }

    fun actualizarMortalidad(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(mortalidad = valor, errorMortalidad = null, errorDescarte = null, errorGeneral = null)
        )
    }

    fun actualizarDescarte(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(descarte = valor, errorDescarte = null, errorMortalidad = null, errorGeneral = null)
        )
    }

    fun actualizarAguaLitros(valor: String) {
        _uiState.value = _uiState.value.copy(aguaLitros = valor, errorAguaLitros = null, errorGeneral = null)
    }

    fun actualizarTemperaturaMax(valor: String) {
        _uiState.value = _uiState.value.copy(temperaturaMax = valor, errorTemperaturaMax = null, errorGeneral = null)
    }

    fun actualizarTemperaturaMin(valor: String) {
        _uiState.value = _uiState.value.copy(temperaturaMin = valor, errorTemperaturaMin = null, errorGeneral = null)
    }

    fun actualizarHorasLuz(valor: String) {
        _uiState.value = _uiState.value.copy(horasLuz = valor, errorHorasLuz = null, errorGeneral = null)
    }

    fun actualizarObservaciones(valor: String) {
        _uiState.value = _uiState.value.copy(observaciones = valor, errorGeneral = null)
    }

    fun actualizarHuevosAA(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosAA = valor, errorHuevosAA = null, errorGeneral = null)
        )
    }

    fun actualizarHuevosA(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosA = valor, errorHuevosA = null, errorGeneral = null)
        )
    }

    fun actualizarHuevosB(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosB = valor, errorHuevosB = null, errorGeneral = null)
        )
    }

    fun actualizarHuevosC(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosC = valor, errorHuevosC = null, errorGeneral = null)
        )
    }

    fun actualizarHuevosRotos(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosRotos = valor, errorHuevosRotos = null, errorGeneral = null)
        )
    }

    fun actualizarHuevosSucios(valor: String) {
        _uiState.value = recalcularMetricasEnVivo(
            _uiState.value.copy(huevosSucios = valor, errorHuevosSucios = null, errorGeneral = null)
        )
    }

    fun limpiarSnackbar() {
        _uiState.value = _uiState.value.copy(mensajeSnackbar = null)
    }

    fun guardarRegistro() {
        val estado = _uiState.value
        val lote = estado.loteSeleccionado
        val esProduccion = lote?.estado == EstadoLote.PRODUCCION

        val validacion = validarFormularioRegistro(
            EntradaFormularioRegistro(
                hayLoteSeleccionado = lote != null,
                fecha = estado.fecha,
                fechaLlegadaLote = lote?.fechaLlegada,
                esProduccion = esProduccion,
                avesVivasAntesDeFecha = estado.avesVivasAntesDeFecha,
                consumoKg = estado.consumoKg,
                mortalidad = estado.mortalidad,
                descarte = estado.descarte,
                aguaLitros = estado.aguaLitros,
                temperaturaMax = estado.temperaturaMax,
                temperaturaMin = estado.temperaturaMin,
                horasLuz = estado.horasLuz,
                observaciones = estado.observaciones,
                huevosAA = estado.huevosAA,
                huevosA = estado.huevosA,
                huevosB = estado.huevosB,
                huevosC = estado.huevosC,
                huevosRotos = estado.huevosRotos,
                huevosSucios = estado.huevosSucios
            )
        )

        val datos = validacion.datos
        if (datos == null || lote == null) {
            val e = validacion.errores
            _uiState.value = estado.copy(
                errorLote = e.errorLote,
                errorFecha = e.errorFecha,
                errorConsumoKg = e.errorConsumoKg,
                errorMortalidad = e.errorMortalidad,
                errorDescarte = e.errorDescarte,
                errorAguaLitros = e.errorAguaLitros,
                errorTemperaturaMax = e.errorTemperaturaMax,
                errorTemperaturaMin = e.errorTemperaturaMin,
                errorHorasLuz = e.errorHorasLuz,
                errorHuevosAA = e.errorHuevosAA,
                errorHuevosA = e.errorHuevosA,
                errorHuevosB = e.errorHuevosB,
                errorHuevosC = e.errorHuevosC,
                errorHuevosRotos = e.errorHuevosRotos,
                errorHuevosSucios = e.errorHuevosSucios
            )
            return
        }

        _uiState.value = estado.copy(guardando = true, errorGeneral = null)

        viewModelScope.launch {
            try {
                val fecha = estado.fecha
                val existenteRegistro = registroDiarioRepository
                    .getPorLoteYFecha(lote.id, fecha)
                    .first()
                val existenteProduccion = if (esProduccion) {
                    produccionHuevoRepository
                        .getPorLoteYFecha(lote.id, fecha)
                        .first()
                } else null

                val registro = RegistroDiario(
                    id = existenteRegistro?.id ?: 0L,
                    loteId = lote.id,
                    fecha = fecha,
                    consumoKg = datos.consumoKg,
                    aguaLitros = datos.aguaLitros,
                    mortalidad = datos.mortalidad,
                    descarte = datos.descarte,
                    temperaturaMax = datos.temperaturaMax,
                    temperaturaMin = datos.temperaturaMin,
                    horasLuz = datos.horasLuz,
                    observaciones = datos.observaciones
                )

                val produccion = if (esProduccion) {
                    ProduccionHuevo(
                        id = existenteProduccion?.id ?: 0L,
                        loteId = lote.id,
                        fecha = fecha,
                        huevosAA = datos.huevosAA,
                        huevosA = datos.huevosA,
                        huevosB = datos.huevosB,
                        huevosC = datos.huevosC,
                        huevosRotos = datos.huevosRotos,
                        huevosSucios = datos.huevosSucios
                    )
                } else null

                db.withTransaction {
                    if (existenteRegistro != null) {
                        registroDiarioRepository.update(registro)
                    } else {
                        registroDiarioRepository.insert(registro)
                    }

                    if (produccion != null) {
                        if (existenteProduccion != null) {
                            produccionHuevoRepository.update(produccion)
                        } else {
                            produccionHuevoRepository.insert(produccion)
                        }
                    }
                }

                val mensaje = if (existenteRegistro != null) {
                    "Registro actualizado correctamente"
                } else {
                    "Registro guardado correctamente"
                }
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeSnackbar = mensaje
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    errorGeneral = "Error al guardar el registro: ${e.message}"
                )
            }
        }
    }

    private fun formatearDecimalCampo(valor: Double, vacioSiCero: Boolean): String {
        if (vacioSiCero && valor == 0.0) return ""
        return if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()
    }

    companion object {
        fun factory(
            loteRepository: LoteRepository,
            registroDiarioRepository: RegistroDiarioRepository,
            produccionHuevoRepository: ProduccionHuevoRepository,
            db: AppDatabase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RegistroViewModel(
                    loteRepository = loteRepository,
                    registroDiarioRepository = registroDiarioRepository,
                    produccionHuevoRepository = produccionHuevoRepository,
                    db = db
                ) as T
        }
    }
}
