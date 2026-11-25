package com.example.granjaavicola.ui.levante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Galpon
import com.example.granjaavicola.data.entity.Lote
import com.example.granjaavicola.data.repository.GalponRepository
import com.example.granjaavicola.data.repository.LoteRepository
import com.example.granjaavicola.domain.calculos.edadEnSemanas
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// ─── Filtros de estado ────────────────────────────────────────────────────────

enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    LEVANTE("Levante"),
    PRODUCCION("Producción"),
    CERRADO("Cerrado")
}

// ─── UI State de la lista ────────────────────────────────────────────────────

data class ItemLote(
    val lote: Lote,
    val nombreGalpon: String,
    val edadSemanas: Int
)

data class ListaLotesUiState(
    val items: List<ItemLote> = emptyList(),
    val filtroActivo: FiltroEstado = FiltroEstado.TODOS,
    val cargando: Boolean = true
)

// ─── UI State del formulario ─────────────────────────────────────────────────

data class FormularioLoteUiState(
    val numeroLote: String = "",
    val galponId: Long? = null,
    val galpones: List<Galpon> = emptyList(),
    val fechaNacimiento: LocalDate? = null,
    val fechaLlegada: LocalDate? = null,
    val avesIniciales: String = "",
    val pesoInicialG: String = "",
    val lineaGenetica: String = "",
    val proveedor: String = "",
    val costoPorPollita: String = "",
    // Nuevo galpón inline
    val mostrandoNuevoGalpon: Boolean = false,
    val nuevoGalponNombre: String = "",
    val nuevoGalponCapacidad: String = "",
    // Validaciones
    val errorNumeroLote: String? = null,
    val errorGalpon: String? = null,
    val errorFechaNacimiento: String? = null,
    val errorFechaLlegada: String? = null,
    val errorAves: String? = null,
    val errorPeso: String? = null,
    val errorCosto: String? = null,
    val errorNuevoGalponNombre: String? = null,
    val errorNuevoGalponCapacidad: String? = null,
    val guardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null
)

// ─── UI State del detalle ────────────────────────────────────────────────────

data class DetalleLoteUiState(
    val lote: Lote? = null,
    val nombreGalpon: String = "",
    val edadSemanas: Int = 0,
    val cargando: Boolean = true,
    val mostrandoConfirmacionCierre: Boolean = false,
    val cerradoExitoso: Boolean = false,
    val errorCierre: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

class LotesViewModel(
    private val loteRepository: LoteRepository,
    private val galponRepository: GalponRepository
) : ViewModel() {

    // ── Lista con filtro ──────────────────────────────────────────────────────

    private val _filtro = MutableStateFlow(FiltroEstado.TODOS)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _lotesFiltrados = _filtro.flatMapLatest { filtro ->
        when (filtro) {
            FiltroEstado.TODOS -> loteRepository.allLotes
            FiltroEstado.LEVANTE -> loteRepository.getPorEstado(EstadoLote.LEVANTE)
            FiltroEstado.PRODUCCION -> loteRepository.getPorEstado(EstadoLote.PRODUCCION)
            FiltroEstado.CERRADO -> loteRepository.getPorEstado(EstadoLote.CERRADO)
        }
    }

    val listaUiState: StateFlow<ListaLotesUiState> =
        combine(_lotesFiltrados, _filtro, galponRepository.allGalpones) { lotes, filtro, galpones ->
            val mapa = galpones.associateBy { it.id }
            val hoy = LocalDate.now()
            ListaLotesUiState(
                items = lotes.map { lote ->
                    ItemLote(
                        lote = lote,
                        nombreGalpon = mapa[lote.galponId]?.nombre ?: "—",
                        edadSemanas = edadEnSemanas(lote.fechaNacimiento, hoy)
                    )
                },
                filtroActivo = filtro,
                cargando = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListaLotesUiState()
        )

    fun cambiarFiltro(filtro: FiltroEstado) {
        _filtro.value = filtro
    }

    // ── Formulario ────────────────────────────────────────────────────────────

    private val _formularioState = MutableStateFlow(FormularioLoteUiState())
    val formularioState: StateFlow<FormularioLoteUiState> = _formularioState

    // Colección reactiva de galpones para el formulario
    init {
        viewModelScope.launch {
            galponRepository.allGalpones.collect { galpones ->
                _formularioState.value = _formularioState.value.copy(galpones = galpones)
            }
        }
    }

    fun actualizarNumeroLote(v: String) {
        _formularioState.value = _formularioState.value.copy(numeroLote = v, errorNumeroLote = null)
    }

    fun seleccionarGalpon(id: Long?) {
        _formularioState.value = _formularioState.value.copy(galponId = id, errorGalpon = null)
    }

    fun actualizarFechaNacimiento(fecha: LocalDate?) {
        _formularioState.value = _formularioState.value.copy(fechaNacimiento = fecha, errorFechaNacimiento = null)
    }

    fun actualizarFechaLlegada(fecha: LocalDate?) {
        _formularioState.value = _formularioState.value.copy(fechaLlegada = fecha, errorFechaLlegada = null)
    }

    fun actualizarAves(v: String) {
        _formularioState.value = _formularioState.value.copy(avesIniciales = v, errorAves = null)
    }

    fun actualizarPeso(v: String) {
        _formularioState.value = _formularioState.value.copy(pesoInicialG = v, errorPeso = null)
    }

    fun actualizarLineaGenetica(v: String) {
        _formularioState.value = _formularioState.value.copy(lineaGenetica = v)
    }

    fun actualizarProveedor(v: String) {
        _formularioState.value = _formularioState.value.copy(proveedor = v)
    }

    fun actualizarCosto(v: String) {
        _formularioState.value = _formularioState.value.copy(costoPorPollita = v, errorCosto = null)
    }

    fun mostrarNuevoGalpon(mostrar: Boolean) {
        _formularioState.value = _formularioState.value.copy(
            mostrandoNuevoGalpon = mostrar,
            nuevoGalponNombre = "",
            nuevoGalponCapacidad = "",
            errorNuevoGalponNombre = null,
            errorNuevoGalponCapacidad = null
        )
    }

    fun actualizarNuevoGalponNombre(v: String) {
        _formularioState.value = _formularioState.value.copy(nuevoGalponNombre = v, errorNuevoGalponNombre = null)
    }

    fun actualizarNuevoGalponCapacidad(v: String) {
        _formularioState.value = _formularioState.value.copy(nuevoGalponCapacidad = v, errorNuevoGalponCapacidad = null)
    }

    fun guardarNuevoGalpon() {
        val estado = _formularioState.value
        var valido = true

        if (estado.nuevoGalponNombre.isBlank()) {
            _formularioState.value = estado.copy(errorNuevoGalponNombre = "Ingresa el nombre del galpón")
            valido = false
        }
        val cap = estado.nuevoGalponCapacidad.toLongOrNull()
        if (cap == null || cap <= 0) {
            _formularioState.value = _formularioState.value.copy(errorNuevoGalponCapacidad = "Capacidad inválida")
            valido = false
        }
        if (!valido) return

        viewModelScope.launch {
            try {
                val nuevoId = galponRepository.insert(
                    Galpon(nombre = estado.nuevoGalponNombre.trim(), capacidad = cap!!.toInt())
                )
                _formularioState.value = _formularioState.value.copy(
                    galponId = nuevoId,
                    mostrandoNuevoGalpon = false,
                    nuevoGalponNombre = "",
                    nuevoGalponCapacidad = "",
                    errorGalpon = null
                )
            } catch (e: Exception) {
                _formularioState.value = _formularioState.value.copy(
                    errorNuevoGalponNombre = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    fun guardarLote() {
        val estado = _formularioState.value
        var valido = true

        // Validaciones
        if (estado.numeroLote.isBlank()) {
            _formularioState.value = estado.copy(errorNumeroLote = "El número de lote es obligatorio")
            valido = false
        }
        if (estado.galponId == null) {
            _formularioState.value = _formularioState.value.copy(errorGalpon = "Selecciona un galpón")
            valido = false
        }
        val hoy = LocalDate.now()
        if (estado.fechaNacimiento == null) {
            _formularioState.value = _formularioState.value.copy(errorFechaNacimiento = "Selecciona la fecha de nacimiento")
            valido = false
        } else if (estado.fechaNacimiento.isAfter(hoy)) {
            _formularioState.value = _formularioState.value.copy(errorFechaNacimiento = "La fecha de nacimiento no puede ser futura")
            valido = false
        }
        if (estado.fechaLlegada == null) {
            _formularioState.value = _formularioState.value.copy(errorFechaLlegada = "Selecciona la fecha de llegada")
            valido = false
        } else if (estado.fechaNacimiento != null && estado.fechaLlegada.isBefore(estado.fechaNacimiento)) {
            _formularioState.value = _formularioState.value.copy(errorFechaLlegada = "La fecha de llegada no puede ser anterior a la fecha de nacimiento")
            valido = false
        }
        val aves = estado.avesIniciales.toIntOrNull()
        if (aves == null || aves <= 0) {
            _formularioState.value = _formularioState.value.copy(errorAves = "Número de aves inválido")
            valido = false
        }
        val peso = estado.pesoInicialG.toDoubleOrNull()
        if (peso == null || peso < 0.0) {
            _formularioState.value = _formularioState.value.copy(errorPeso = "Peso inválido (gramos)")
            valido = false
        }
        val costo = estado.costoPorPollita.toLongOrNull()
        if (costo == null || costo < 0L) {
            _formularioState.value = _formularioState.value.copy(errorCosto = "Costo inválido")
            valido = false
        }
        if (!valido) return

        _formularioState.value = _formularioState.value.copy(guardando = true, errorGeneral = null)

        viewModelScope.launch {
            try {
                // Verificar número de lote duplicado
                val numeroBuscado = estado.numeroLote.trim()
                val listaLotes = loteRepository.allLotes.first()
                val duplicado = listaLotes.any { it.numeroLote.equals(numeroBuscado, ignoreCase = true) }
                if (duplicado) {
                    _formularioState.value = _formularioState.value.copy(
                        guardando = false,
                        errorNumeroLote = "Ya existe un lote con ese número"
                    )
                    return@launch
                }

                loteRepository.insert(
                    Lote(
                        numeroLote = numeroBuscado,
                        galponId = estado.galponId!!,
                        fechaNacimiento = estado.fechaNacimiento!!,
                        fechaLlegada = estado.fechaLlegada!!,
                        avesIniciales = aves!!,
                        pesoInicialG = peso!!,
                        lineaGenetica = estado.lineaGenetica.trim(),
                        proveedor = estado.proveedor.trim(),
                        costoPorPollita = costo!!,
                        estado = EstadoLote.LEVANTE
                    )
                )
                _formularioState.value = _formularioState.value.copy(
                    guardando = false,
                    guardadoExitoso = true
                )
            } catch (e: Exception) {
                _formularioState.value = _formularioState.value.copy(
                    guardando = false,
                    errorGeneral = "Error al guardar el lote: ${e.message}"
                )
            }
        }
    }

    fun reiniciarFormulario() {
        _formularioState.value = FormularioLoteUiState(
            galpones = _formularioState.value.galpones
        )
    }

    // ── Detalle ───────────────────────────────────────────────────────────────

    private val _detalleState = MutableStateFlow(DetalleLoteUiState())
    val detalleState: StateFlow<DetalleLoteUiState> = _detalleState
    private var cargarLoteJob: Job? = null

    fun cargarLote(loteId: Long) {
        cargarLoteJob?.cancel()
        _detalleState.value = DetalleLoteUiState(
            cargando = true,
            cerradoExitoso = false,
            mostrandoConfirmacionCierre = false
        )
        cargarLoteJob = viewModelScope.launch {
            combine(
                loteRepository.getById(loteId),
                galponRepository.allGalpones
            ) { lote, galpones ->
                lote to galpones
            }.collect { (lote, galpones) ->
                if (lote != null) {
                    val galpon = galpones.firstOrNull { it.id == lote.galponId }
                    _detalleState.value = _detalleState.value.copy(
                        lote = lote,
                        nombreGalpon = galpon?.nombre ?: "—",
                        edadSemanas = edadEnSemanas(lote.fechaNacimiento, LocalDate.now()),
                        cargando = false
                    )
                } else {
                    _detalleState.value = _detalleState.value.copy(
                        lote = null,
                        cargando = false
                    )
                }
            }
        }
    }

    fun mostrarConfirmacionCierre(mostrar: Boolean) {
        _detalleState.value = _detalleState.value.copy(mostrandoConfirmacionCierre = mostrar)
    }

    fun cerrarLote() {
        val loteActual = _detalleState.value.lote ?: return
        viewModelScope.launch {
            try {
                loteRepository.update(
                    loteActual.copy(
                        estado = EstadoLote.CERRADO,
                        fechaCierre = LocalDate.now()
                    )
                )
                _detalleState.value = _detalleState.value.copy(
                    mostrandoConfirmacionCierre = false,
                    cerradoExitoso = true,
                    errorCierre = null
                )
            } catch (e: Exception) {
                _detalleState.value = _detalleState.value.copy(
                    mostrandoConfirmacionCierre = false,
                    errorCierre = "Error al cerrar el lote: ${e.message}"
                )
            }
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    companion object {
        fun factory(
            loteRepository: LoteRepository,
            galponRepository: GalponRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LotesViewModel(loteRepository, galponRepository) as T
        }
    }
}
