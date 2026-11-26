package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.TarifaRetencion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TarifaRetencionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var tarifaRetencionDao: TarifaRetencionDao

    private val tarifasSemilla = listOf(
        TarifaRetencion("Compras generales", 0.025, 10),
        TarifaRetencion("Servicios generales", 0.04, 2),
        TarifaRetencion("Honorarios y comisiones", 0.11, 0),
        TarifaRetencion("Arrendamientos", 0.035, 10),
        TarifaRetencion(
            "Compra de productos agrícolas o pecuarios sin procesamiento industrial",
            0.015, 70
        ),
        TarifaRetencion("Transporte de carga", 0.01, 4),
        TarifaRetencion("Sin retención", 0.0, 0),
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        tarifaRetencionDao = db.tarifaRetencionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarAllYListar() = runBlocking {
        tarifaRetencionDao.insertAll(tarifasSemilla)
        val todas = tarifaRetencionDao.getAll().first()
        assertEquals(tarifasSemilla.size, todas.size)
    }

    @Test
    fun obtenerPorConcepto() = runBlocking {
        tarifaRetencionDao.insertAll(tarifasSemilla)
        val tarifa = tarifaRetencionDao.getByConcepto("Honorarios y comisiones").first()

        assertNotNull(tarifa)
        assertEquals("Honorarios y comisiones", tarifa?.concepto)
        assertEquals(0.11, tarifa?.tarifa ?: 0.0, 0.0001)
        assertEquals(0, tarifa?.baseMinimaUvt)
    }

    @Test
    fun tarifaAgropecuariaCorrectaParaPollotas() = runBlocking {
        tarifaRetencionDao.insertAll(tarifasSemilla)
        val tarifa = tarifaRetencionDao
            .getByConcepto(
                "Compra de productos agrícolas o pecuarios sin procesamiento industrial"
            )
            .first()

        assertNotNull(tarifa)
        assertEquals(0.015, tarifa?.tarifa ?: 0.0, 0.0001)
        assertEquals(70, tarifa?.baseMinimaUvt)
    }

    @Test
    fun tarifaCeroParaSinRetencion() = runBlocking {
        tarifaRetencionDao.insertAll(tarifasSemilla)
        val tarifa = tarifaRetencionDao.getByConcepto("Sin retención").first()

        assertNotNull(tarifa)
        assertEquals(0.0, tarifa?.tarifa ?: 1.0, 0.0001)
        assertEquals(0, tarifa?.baseMinimaUvt)
    }

    @Test
    fun actualizarTarifa() = runBlocking {
        val original = TarifaRetencion("Compras generales", 0.025, 10)
        tarifaRetencionDao.insert(original)

        val actualizada = original.copy(tarifa = 0.03, baseMinimaUvt = 15)
        tarifaRetencionDao.update(actualizada)

        val recuperada = tarifaRetencionDao.getByConcepto("Compras generales").first()
        assertEquals(0.03, recuperada?.tarifa ?: 0.0, 0.0001)
        assertEquals(15, recuperada?.baseMinimaUvt)
    }

    @Test
    fun insertOrReplaceEnConflicto() = runBlocking {
        tarifaRetencionDao.insert(TarifaRetencion("Compras generales", 0.025, 10))
        tarifaRetencionDao.insert(TarifaRetencion("Compras generales", 0.03, 12))

        val recuperada = tarifaRetencionDao.getByConcepto("Compras generales").first()
        assertEquals(0.03, recuperada?.tarifa ?: 0.0, 0.0001)
        assertEquals(12, recuperada?.baseMinimaUvt)
        assertEquals(1, tarifaRetencionDao.count())
    }

    @Test
    fun borrarTarifa() = runBlocking {
        val tarifa = TarifaRetencion("Transporte de carga", 0.01, 4)
        tarifaRetencionDao.insert(tarifa)
        assertEquals(1, tarifaRetencionDao.count())

        tarifaRetencionDao.delete(tarifa)
        assertEquals(0, tarifaRetencionDao.count())
    }
}
