package com.example.granjaavicola

import android.app.Application
import android.util.Log
import androidx.room.withTransaction
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.preferences.PreferenciasRepository
import com.example.granjaavicola.data.repository.CompraRepository
import com.example.granjaavicola.data.repository.EstandarRepository
import com.example.granjaavicola.data.repository.GalponRepository
import com.example.granjaavicola.data.repository.InventarioAlimentoMovimientoRepository
import com.example.granjaavicola.data.repository.LoteRepository
import com.example.granjaavicola.data.repository.PagoRepository
import com.example.granjaavicola.data.repository.PesajeAveRepository
import com.example.granjaavicola.data.repository.PesajeHuevoRepository
import com.example.granjaavicola.data.repository.ProduccionHuevoRepository
import com.example.granjaavicola.data.repository.RegistroDiarioRepository
import com.example.granjaavicola.data.repository.SanidadRepository
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
}

class GranjaApp : Application() {

    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        precargarEstandaresSiVacio()
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
}
