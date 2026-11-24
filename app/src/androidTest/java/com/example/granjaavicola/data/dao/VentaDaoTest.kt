package com.example.granjaavicola.data.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Galpon
import com.example.granjaavicola.data.entity.Lote
import com.example.granjaavicola.data.entity.ProductoVenta
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoTercero
import com.example.granjaavicola.data.entity.Venta
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class VentaDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var galponDao: GalponDao
    private lateinit var loteDao: LoteDao
    private lateinit var terceroDao: TerceroDao
    private lateinit var ventaDao: VentaDao

    private var loteId: Long = 0
    private var clienteId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        galponDao = db.galponDao()
        loteDao = db.loteDao()
        terceroDao = db.terceroDao()
        ventaDao = db.ventaDao()

        val galponId = galponDao.insert(Galpon(nombre = "Galpón Producción 1", capacidad = 6000))
        loteId = loteDao.insert(
            Lote(
                numeroLote = "LOTE-VENTA-01",
                galponId = galponId,
                fechaNacimiento = LocalDate.of(2025, 6, 1),
                fechaLlegada = LocalDate.of(2025, 6, 2),
                avesIniciales = 6000,
                pesoInicialG = 41.5,
                lineaGenetica = "Lohmann Brown",
                proveedor = "Incubadora",
                costoPorPollita = 3600L,
                estado = EstadoLote.PRODUCCION,
            )
        )

        clienteId = terceroDao.insert(
            Tercero(
                nombre = "Supermercado El Sol",
                documento = "901987654-1",
                tipo = TipoTercero.CLIENTE,
                telefono = "3159876543",
                direccion = "Carrera 5 # 14-25",
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerVentaPorId() = runBlocking {
        val fecha = LocalDate.of(2026, 2, 10)
        val venta = Venta(
            fecha = fecha,
            terceroId = clienteId,
            producto = ProductoVenta.HUEVO_AA,
            cantidad = 50.0,
            unidad = "CANASTILLA_30_DOCENAS",
            valorUnitario = 180_000L,
            iva = 0L,
            retencion = 0L,
            total = 9_000_000L,
            loteId = loteId,
        )

        val id = ventaDao.insert(venta)
        val recuperada = ventaDao.getById(id).first()

        assertNotNull(recuperada)
        assertEquals(clienteId, recuperada?.terceroId)
        assertEquals(loteId, recuperada?.loteId)
        assertEquals(ProductoVenta.HUEVO_AA, recuperada?.producto)
        assertEquals(50.0, recuperada?.cantidad ?: 0.0, 0.001)
        assertEquals(180_000L, recuperada?.valorUnitario)
        assertEquals(9_000_000L, recuperada?.total)
    }

    @Test
    fun obtenerVentasPorRangoFecha() = runBlocking {
        val v1 = Venta(
            fecha = LocalDate.of(2026, 2, 5),
            terceroId = clienteId,
            producto = ProductoVenta.HUEVO_A,
            cantidad = 30.0,
            unidad = "CANASTILLA",
            valorUnitario = 160_000L,
            total = 4_800_000L,
            loteId = loteId,
        )
        val v2 = Venta(
            fecha = LocalDate.of(2026, 2, 15),
            terceroId = clienteId,
            producto = ProductoVenta.GALLINAZA,
            cantidad = 100.0,
            unidad = "BULTO",
            valorUnitario = 5_000L,
            total = 500_000L,
            loteId = loteId,
        )
        val v3 = Venta(
            fecha = LocalDate.of(2026, 3, 2),
            terceroId = clienteId,
            producto = ProductoVenta.HUEVO_B,
            cantidad = 20.0,
            unidad = "CANASTILLA",
            valorUnitario = 140_000L,
            total = 2_800_000L,
            loteId = loteId,
        )

        ventaDao.insert(v1)
        ventaDao.insert(v2)
        ventaDao.insert(v3)

        val ventasFebrero = ventaDao.getPorRangoFecha(
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 28)
        ).first()

        assertEquals(2, ventasFebrero.size)
        assertEquals(LocalDate.of(2026, 2, 15), ventasFebrero[0].fecha)
        assertEquals(LocalDate.of(2026, 2, 5), ventasFebrero[1].fecha)
    }

    @Test
    fun restriccionBorrarTerceroConVentaAsociada(): Unit = runBlocking {
        val venta = Venta(
            fecha = LocalDate.of(2026, 2, 10),
            terceroId = clienteId,
            producto = ProductoVenta.HUEVO_AA,
            cantidad = 10.0,
            unidad = "CANASTILLA",
            valorUnitario = 180_000L,
            total = 1_800_000L,
        )
        ventaDao.insert(venta)

        val cliente = terceroDao.getById(clienteId).first()!!
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                terceroDao.delete(cliente)
            }
        }
    }

    @Test
    fun restriccionBorrarLoteConVentaAsociada(): Unit = runBlocking {
        val venta = Venta(
            fecha = LocalDate.of(2026, 2, 10),
            terceroId = null,
            producto = ProductoVenta.GALLINA_DESCARTE,
            cantidad = 200.0,
            unidad = "AVE",
            valorUnitario = 8_000L,
            total = 1_600_000L,
            loteId = loteId,
        )
        ventaDao.insert(venta)

        val lote = loteDao.getById(loteId).first()!!
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                loteDao.delete(lote)
            }
        }
    }

    @Test
    fun borrarVenta() = runBlocking {
        val venta = Venta(
            fecha = LocalDate.of(2026, 2, 12),
            terceroId = clienteId,
            producto = ProductoVenta.OTRO,
            cantidad = 1.0,
            unidad = "LOTE",
            valorUnitario = 50_000L,
            total = 50_000L
        )
        val id = ventaDao.insert(venta)
        val recuperada = ventaDao.getById(id).first()!!

        ventaDao.delete(recuperada)
        assertNull(ventaDao.getById(id).first())
    }
}
