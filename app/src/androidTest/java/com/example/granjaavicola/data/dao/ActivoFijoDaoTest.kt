package com.example.granjaavicola.data.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.ActivoFijo
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoDocumento
import com.example.granjaavicola.data.entity.TipoTercero
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ActivoFijoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var activoFijoDao: ActivoFijoDao
    private lateinit var terceroDao: TerceroDao

    private var proveedorId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activoFijoDao = db.activoFijoDao()
        terceroDao = db.terceroDao()

        proveedorId = terceroDao.insert(
            Tercero(
                nombre = "Proveedora de Equipos SA",
                tipoDocumento = TipoDocumento.NIT,
                documento = "800123456",
                digitoVerificacion = 2,
                tipo = TipoTercero.PROVEEDOR,
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerActivoFijoPorId() = runBlocking {
        val fechaCompra = LocalDate.of(2025, 5, 20)
        val activo = ActivoFijo(
            codigo = "EQU-001",
            descripcion = "Planta Eléctrica Diésel 15kVA",
            fechaCompra = fechaCompra,
            terceroId = proveedorId,
            factura = "FE-2025-00123",
            valorCompra = 28_000_000L,
            vidaUtilMeses = 120,
            depreciacionAcumulada = 2_100_000L
        )

        val id = activoFijoDao.insert(activo)
        val recuperado = activoFijoDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals("EQU-001", recuperado?.codigo)
        assertEquals("Planta Eléctrica Diésel 15kVA", recuperado?.descripcion)
        assertEquals(fechaCompra, recuperado?.fechaCompra)
        assertEquals(proveedorId, recuperado?.terceroId)
        assertEquals("FE-2025-00123", recuperado?.factura)
        assertEquals(28_000_000L, recuperado?.valorCompra)
        assertEquals(120, recuperado?.vidaUtilMeses)
        assertEquals(2_100_000L, recuperado?.depreciacionAcumulada)
    }

    @Test
    fun listarActivosFijosYActualizar() = runBlocking {
        val activo = ActivoFijo(
            codigo = "EQU-002",
            descripcion = "Balanza Digital Industrial 150kg",
            fechaCompra = LocalDate.of(2025, 8, 10),
            valorCompra = 1_850_000L,
            vidaUtilMeses = 60,
            depreciacionAcumulada = 150_000L
        )

        val id = activoFijoDao.insert(activo)
        val todos = activoFijoDao.getAll().first()
        assertEquals(1, todos.size)

        val actualizado = todos[0].copy(depreciacionAcumulada = 300_000L)
        activoFijoDao.update(actualizado)

        val recuperado = activoFijoDao.getById(id).first()
        assertEquals(300_000L, recuperado?.depreciacionAcumulada)
    }

    @Test
    fun activoSinTerceroSeInsertaCorrectamente() = runBlocking {
        val activo = ActivoFijo(
            codigo = "EQU-003",
            descripcion = "Comedero automático",
            fechaCompra = LocalDate.of(2026, 1, 10),
            terceroId = null,
            factura = null,
            valorCompra = 500_000L,
            vidaUtilMeses = 36,
        )
        val id = activoFijoDao.insert(activo)
        val recuperado = activoFijoDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals(null, recuperado?.terceroId)
        assertEquals(null, recuperado?.factura)
    }

    @Test
    fun restriccionBorrarTerceroConActivoAsociado(): Unit = runBlocking {
        val activo = ActivoFijo(
            codigo = "EQU-004",
            descripcion = "Ventilador industrial",
            fechaCompra = LocalDate.of(2026, 2, 1),
            terceroId = proveedorId,
            valorCompra = 1_200_000L,
            vidaUtilMeses = 48,
        )
        activoFijoDao.insert(activo)

        val tercero = terceroDao.getById(proveedorId).first()!!
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                terceroDao.delete(tercero)
            }
        }
    }
}
