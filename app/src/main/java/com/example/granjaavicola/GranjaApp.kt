package com.example.granjaavicola

import android.app.Application
import com.example.granjaavicola.data.db.AppDatabase
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

/**
 * Contenedor manual de dependencias de la aplicación.
 * Creado una sola vez en GranjaApp y accesible desde cualquier contexto
 * mediante (application as GranjaApp).container.
 */
class AppContainer(application: Application) {

    val database: AppDatabase = AppDatabase.getDatabase(application)

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

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
