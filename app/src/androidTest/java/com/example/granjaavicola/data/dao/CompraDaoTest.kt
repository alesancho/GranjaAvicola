package com.example.granjaavicola.data.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.CategoriaCompra
import com.example.granjaavicola.data.entity.Compra
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Galpon
import com.example.granjaavicola.data.entity.Lote
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoTercero
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
class CompraDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var galponDao: GalponDao
    private lateinit var loteDao: LoteDao
    private lateinit var terceroDao: TerceroDao
    private lateinit var compraDao: CompraDao

    private var loteId: Long = 0
    private var proveedorId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        galponDao = db.galponDao()
        loteDao = db.loteDao()
        terceroDao = db.terceroDao()
        compraDao = db.compraDao()

        val galponId = galponDao.insert(Galpon(nombre = "Galpón 1", capacidad = 5000))
        loteId = loteDao.insert(
            Lote(
                numeroLote = "LOTE-COMPRA-01",
                galponId = galponId,
                fechaNacimiento = LocalDate.of(2026, 1, 1),
                fechaLlegada = LocalDate.of(2026, 1, 2),
                avesIniciales = 5000,
                pesoInicialG = 42.0,
                lineaGenetica = "Hy-Line Brown",
                proveedor = "Incubadora",
                costoPorPollita = 3500L,
                estado = EstadoLote.LEVANTE,
            )
        )

        proveedorId = terceroDao.insert(
            Tercero(
                nombre = "Distribuidora Avícola SAS",
                documento = "900123456-7",
                tipo = TipoTercero.PROVEEDOR,
                telefono = "3001234567",
                direccion = "Calle 10 # 20-30",
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerCompraPorId() = runBlocking {
        val fecha = LocalDate.of(2026, 2, 1)
        val compra = Compra(
            fecha = fecha,
            terceroId = proveedorId,
            categoria = CategoriaCompra.ALIMENTO,
            descripcion = "Bultos concentrado levante",
            cantidad = 40.0,
            unidad = "BULTOS_40KG",
            valorUnitario = 120_000L,
            iva = 0L,
            retencion = 0L,
            total = 4_800_000L,
            loteId = loteId,
            pagada = false,
        )

        val id = compraDao.insert(compra)
        val recuperada = compraDao.getById(id).first()

        assertNotNull(recuperada)
        assertEquals(proveedorId, recuperada?.terceroId)
        assertEquals(loteId, recuperada?.loteId)
        assertEquals(CategoriaCompra.ALIMENTO, recuperada?.categoria)
        assertEquals(40.0, recuperada?.cantidad ?: 0.0, 0.001)
        assertEquals(120_000L, recuperada?.valorUnitario)
        assertEquals(4_800_000L, recuperada?.total)
        assertEquals(false, recuperada?.pagada)
    }

    @Test
    fun obtenerComprasPorRangoFecha() = runBlocking {
        val c1 = Compra(
            fecha = LocalDate.of(2026, 2, 5),
            terceroId = proveedorId,
            categoria = CategoriaCompra.MEDICAMENTOS,
            descripcion = "Vitaminas complejo B",
            cantidad = 5.0,
            unidad = "FRASCO_1L",
            valorUnitario = 45_000L,
            total = 225_000L,
            loteId = loteId,
            pagada = true,
        )
        val c2 = Compra(
            fecha = LocalDate.of(2026, 2, 15),
            terceroId = proveedorId,
            categoria = CategoriaCompra.ALIMENTO,
            descripcion = "Alimento etapa 2",
            cantidad = 20.0,
            unidad = "BULTOS_40KG",
            valorUnitario = 125_000L,
            total = 2_500_000L,
            loteId = loteId,
            pagada = false,
        )
        val c3 = Compra(
            fecha = LocalDate.of(2026, 3, 1),
            terceroId = proveedorId,
            categoria = CategoriaCompra.EMPAQUES,
            descripcion = "Maples cartón",
            cantidad = 500.0,
            unidad = "UNIDAD",
            valorUnitario = 350L,
            total = 175_000L,
            pagada = true,
        )

        compraDao.insert(c1)
        compraDao.insert(c2)
        compraDao.insert(c3)

        val enFebrero = compraDao.getPorRangoFecha(
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 28)
        ).first()

        assertEquals(2, enFebrero.size)
        assertEquals(LocalDate.of(2026, 2, 15), enFebrero[0].fecha)
        assertEquals(LocalDate.of(2026, 2, 5), enFebrero[1].fecha)
    }

    @Test
    fun restriccionBorrarTerceroConCompraAsociada(): Unit = runBlocking {
        val compra = Compra(
            fecha = LocalDate.of(2026, 2, 10),
            terceroId = proveedorId,
            categoria = CategoriaCompra.SERVICIOS,
            descripcion = "Servicio veterinario",
            cantidad = 1.0,
            unidad = "SERVICIO",
            valorUnitario = 200_000L,
            total = 200_000L,
            pagada = true,
        )
        compraDao.insert(compra)

        val tercero = terceroDao.getById(proveedorId).first()!!
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                terceroDao.delete(tercero)
            }
        }
    }

    @Test
    fun restriccionBorrarLoteConCompraAsociada(): Unit = runBlocking {
        val compra = Compra(
            fecha = LocalDate.of(2026, 2, 10),
            terceroId = null,
            categoria = CategoriaCompra.MANO_OBRA,
            descripcion = "Jornal vacunación",
            cantidad = 2.0,
            unidad = "JORNAL",
            valorUnitario = 60_000L,
            total = 120_000L,
            loteId = loteId,
            pagada = true,
        )
        compraDao.insert(compra)

        val lote = loteDao.getById(loteId).first()!!
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                loteDao.delete(lote)
            }
        }
    }

    @Test
    fun actualizarYBorrarCompra() = runBlocking {
        val compra = Compra(
            fecha = LocalDate.of(2026, 2, 20),
            terceroId = proveedorId,
            categoria = CategoriaCompra.OTROS,
            descripcion = "Bombillos galpón",
            cantidad = 10.0,
            unidad = "UNIDAD",
            valorUnitario = 15_000L,
            total = 150_000L,
            pagada = false
        )
        val id = compraDao.insert(compra)

        val existente = compraDao.getById(id).first()!!
        val actualizada = existente.copy(pagada = true)
        compraDao.update(actualizada)

        val recuperada = compraDao.getById(id).first()
        assertEquals(true, recuperada?.pagada)

        compraDao.delete(actualizada)
        assertNull(compraDao.getById(id).first())
    }
}
