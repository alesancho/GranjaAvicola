package com.example.granjaavicola

import android.app.Application
import android.util.Log
import androidx.room.withTransaction
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.FaseConcepto
import com.example.granjaavicola.data.entity.ConceptoCompra
import com.example.granjaavicola.data.entity.TarifaRetencion
import com.example.granjaavicola.data.preferences.PreferenciasRepository
import com.example.granjaavicola.data.repository.ActivoFijoRepository
import com.example.granjaavicola.data.repository.CompraRepository
import com.example.granjaavicola.data.repository.ConceptoCompraRepository
import com.example.granjaavicola.data.repository.EmpleadoRepository
import com.example.granjaavicola.data.repository.EstandarRepository
import com.example.granjaavicola.data.repository.GalponRepository
import com.example.granjaavicola.data.repository.InventarioAlimentoMovimientoRepository
import com.example.granjaavicola.data.repository.LoteRepository
import com.example.granjaavicola.data.repository.MovimientoBancarioRepository
import com.example.granjaavicola.data.repository.PagoNominaRepository
import com.example.granjaavicola.data.repository.PagoRepository
import com.example.granjaavicola.data.repository.ParametrosFiscalesRepository
import com.example.granjaavicola.data.repository.PesajeAveRepository
import com.example.granjaavicola.data.repository.PesajeHuevoRepository
import com.example.granjaavicola.data.repository.ProduccionHuevoRepository
import com.example.granjaavicola.data.repository.RegistroDiarioRepository
import com.example.granjaavicola.data.repository.SanidadRepository
import com.example.granjaavicola.data.repository.TarifaRetencionRepository
import com.example.granjaavicola.data.repository.TerceroRepository
import com.example.granjaavicola.data.repository.VentaRepository
import com.example.granjaavicola.domain.estandares.parsearEstandaresCsv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor manual de dependencias de la aplicación.
 * Creado una sola vez en GranjaApp y accesible desde cualquier contexto
 * mediante (application as GranjaApp).container.
 */
class AppContainer(application: Application) {

    val database: AppDatabase = AppDatabase.getDatabase(application)

    val preferenciasRepository: PreferenciasRepository =
        PreferenciasRepository(application)

    val galponRepository: GalponRepository =
        GalponRepository(database.galponDao())

    val loteRepository: LoteRepository =
        LoteRepository(database.loteDao())

    val registroDiarioRepository: RegistroDiarioRepository =
        RegistroDiarioRepository(database.registroDiarioDao())

    val produccionHuevoRepository: ProduccionHuevoRepository =
        ProduccionHuevoRepository(database.produccionHuevoDao())

    val pesajeAveRepository: PesajeAveRepository =
        PesajeAveRepository(database.pesajeAveDao())

    val pesajeHuevoRepository: PesajeHuevoRepository =
        PesajeHuevoRepository(database.pesajeHuevoDao())

    val estandarRepository: EstandarRepository =
        EstandarRepository(database.estandarDao())

    val terceroRepository: TerceroRepository =
        TerceroRepository(database.terceroDao())

    val compraRepository: CompraRepository =
        CompraRepository(database.compraDao())

    val ventaRepository: VentaRepository =
        VentaRepository(database.ventaDao())

    val pagoRepository: PagoRepository =
        PagoRepository(database.pagoDao())

    val inventarioAlimentoMovimientoRepository: InventarioAlimentoMovimientoRepository =
        InventarioAlimentoMovimientoRepository(database.inventarioAlimentoMovimientoDao())

    val sanidadRepository: SanidadRepository =
        SanidadRepository(database.sanidadDao())

    val movimientoBancarioRepository: MovimientoBancarioRepository =
        MovimientoBancarioRepository(database.movimientoBancarioDao())

    val empleadoRepository: EmpleadoRepository =
        EmpleadoRepository(database.empleadoDao())

    val pagoNominaRepository: PagoNominaRepository =
        PagoNominaRepository(database.pagoNominaDao())

    val activoFijoRepository: ActivoFijoRepository =
        ActivoFijoRepository(database.activoFijoDao())

    val conceptoCompraRepository: ConceptoCompraRepository =
        ConceptoCompraRepository(database.conceptoCompraDao())

    val parametrosFiscalesRepository: ParametrosFiscalesRepository =
        ParametrosFiscalesRepository(database.parametrosFiscalesDao())

    val tarifaRetencionRepository: TarifaRetencionRepository =
        TarifaRetencionRepository(database.tarifaRetencionDao())
}

class GranjaApp : Application() {

    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        precargarEstandaresSiVacio()
        precargarConceptosSiVacio()
        precargarTarifasSiVacio()
    }

    private fun precargarEstandaresSiVacio() {
        appScope.launch(Dispatchers.IO) {
            try {
                if (container.estandarRepository.count() == 0) {
                    val textoCsv = assets.open("estandares.csv")
                        .bufferedReader(Charsets.UTF_8)
                        .use { it.readText() }
                    val estandares = parsearEstandaresCsv(textoCsv)
                    if (estandares.isNotEmpty()) {
                        container.database.withTransaction {
                            if (container.estandarRepository.count() == 0) {
                                container.estandarRepository.insertAll(estandares)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GranjaApp", "Error al cargar estándares desde assets", e)
            }
        }
    }

    private fun precargarConceptosSiVacio() {
        appScope.launch(Dispatchers.IO) {
            try {
                if (container.conceptoCompraRepository.count() == 0) {
                    val textoCsv = assets.open("conceptos.csv")
                        .bufferedReader(Charsets.UTF_8)
                        .use { it.readText() }
                    val conceptos = parsearConceptosCsv(textoCsv)
                    if (conceptos.isNotEmpty()) {
                        container.database.withTransaction {
                            if (container.conceptoCompraRepository.count() == 0) {
                                container.conceptoCompraRepository.insertAll(conceptos)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GranjaApp", "Error al cargar conceptos desde assets", e)
            }
        }
    }

    private fun precargarTarifasSiVacio() {
        appScope.launch(Dispatchers.IO) {
            try {
                if (container.tarifaRetencionRepository.count() == 0) {
                    val tarifas = tarifasRetencionPorDefecto()
                    container.database.withTransaction {
                        if (container.tarifaRetencionRepository.count() == 0) {
                            container.tarifaRetencionRepository.insertAll(tarifas)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GranjaApp", "Error al cargar tarifas de retención", e)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers de parseo y datos semilla (top-level, fuera de GranjaApp)
// ---------------------------------------------------------------------------

private fun parsearConceptosCsv(contenido: String): List<ConceptoCompra> {
    if (contenido.isBlank()) return emptyList()
    val texto = contenido.removePrefix("\uFEFF")
    val resultado = mutableListOf<ConceptoCompra>()
    for (lineaCruda in texto.lineSequence()) {
        val linea = lineaCruda.trim()
        if (linea.isEmpty()) continue
        val cols = linea.split(",")
        if (cols.size < 5) continue
        if (cols[0].trim().equals("codigo", ignoreCase = true)) continue // encabezado
        val codigo = cols[0].trim()
        if (codigo.isEmpty()) continue
        val nombre = cols[1].trim()
        val faseStr = cols[2].trim().uppercase()
        val fase = runCatching { FaseConcepto.valueOf(faseStr) }.getOrNull() ?: continue
        val naturaleza = cols[3].trim()
        val retencionSugerida = cols[4].trim()
        resultado.add(
            ConceptoCompra(
                codigo = codigo,
                nombre = nombre,
                fase = fase,
                naturaleza = naturaleza,
                retencionSugerida = retencionSugerida
            )
        )
    }
    return resultado
}

private fun tarifasRetencionPorDefecto(): List<TarifaRetencion> = listOf(
    TarifaRetencion(
        concepto = "Compras generales",
        tarifa = 0.025,
        baseMinimaUvt = 10
    ),
    TarifaRetencion(
        concepto = "Servicios generales",
        tarifa = 0.04,
        baseMinimaUvt = 2
    ),
    TarifaRetencion(
        concepto = "Honorarios y comisiones",
        tarifa = 0.11,
        baseMinimaUvt = 0
    ),
    TarifaRetencion(
        concepto = "Arrendamientos",
        tarifa = 0.035,
        baseMinimaUvt = 10
    ),
    TarifaRetencion(
        concepto = "Compra de productos agrícolas o pecuarios sin procesamiento industrial",
        tarifa = 0.015,
        baseMinimaUvt = 70
    ),
    TarifaRetencion(
        concepto = "Transporte de carga",
        tarifa = 0.01,
        baseMinimaUvt = 4
    ),
    TarifaRetencion(
        concepto = "Sin retención",
        tarifa = 0.0,
        baseMinimaUvt = 0
    )
)
