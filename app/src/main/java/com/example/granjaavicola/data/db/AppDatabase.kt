package com.example.granjaavicola.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.granjaavicola.data.dao.CompraDao
import com.example.granjaavicola.data.dao.EstandarDao
import com.example.granjaavicola.data.dao.GalponDao
import com.example.granjaavicola.data.dao.InventarioAlimentoMovimientoDao
import com.example.granjaavicola.data.dao.LoteDao
import com.example.granjaavicola.data.dao.PagoDao
import com.example.granjaavicola.data.dao.PesajeAveDao
import com.example.granjaavicola.data.dao.PesajeHuevoDao
import com.example.granjaavicola.data.dao.ProduccionHuevoDao
import com.example.granjaavicola.data.dao.RegistroDiarioDao
import com.example.granjaavicola.data.dao.SanidadDao
import com.example.granjaavicola.data.dao.TerceroDao
import com.example.granjaavicola.data.dao.VentaDao
import com.example.granjaavicola.data.entity.Compra
import com.example.granjaavicola.data.entity.Estandar
import com.example.granjaavicola.data.entity.Galpon
import com.example.granjaavicola.data.entity.InventarioAlimentoMovimiento
import com.example.granjaavicola.data.entity.Lote
import com.example.granjaavicola.data.entity.Pago
import com.example.granjaavicola.data.entity.PesajeAve
import com.example.granjaavicola.data.entity.PesajeHuevo
import com.example.granjaavicola.data.entity.ProduccionHuevo
import com.example.granjaavicola.data.entity.RegistroDiario
import com.example.granjaavicola.data.entity.Sanidad
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.Venta

@Database(
    entities = [
        Galpon::class,
        Lote::class,
        RegistroDiario::class,
        ProduccionHuevo::class,
        PesajeAve::class,
        PesajeHuevo::class,
        Estandar::class,
        Tercero::class,
        Compra::class,
        Venta::class,
        Pago::class,
        InventarioAlimentoMovimiento::class,
        Sanidad::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun galponDao(): GalponDao
    abstract fun loteDao(): LoteDao
    abstract fun registroDiarioDao(): RegistroDiarioDao
    abstract fun produccionHuevoDao(): ProduccionHuevoDao
    abstract fun pesajeAveDao(): PesajeAveDao
    abstract fun pesajeHuevoDao(): PesajeHuevoDao
    abstract fun estandarDao(): EstandarDao
    abstract fun terceroDao(): TerceroDao
    abstract fun compraDao(): CompraDao
    abstract fun ventaDao(): VentaDao
    abstract fun pagoDao(): PagoDao
    abstract fun inventarioAlimentoMovimientoDao(): InventarioAlimentoMovimientoDao
    abstract fun sanidadDao(): SanidadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "granja_avicola_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
