package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.ConceptoCompra
import com.example.granjaavicola.data.entity.FaseConcepto
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
class ConceptoCompraDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var conceptoCompraDao: ConceptoCompraDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        conceptoCompraDao = db.conceptoCompraDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun conceptosBase() = listOf(
        ConceptoCompra("POL", "Pollitas de un día", FaseConcepto.LEVANTE, "Costo directo",
            "Compra de productos agrícolas o pecuarios sin procesamiento industrial"),
        ConceptoCompra("ALI-LEV", "Alimento concentrado levante", FaseConcepto.LEVANTE,
            "Costo directo", "Compras generales"),
        ConceptoCompra("ALI-PRO", "Alimento concentrado producción", FaseConcepto.PRODUCCION,
            "Costo directo", "Compras generales"),
        ConceptoCompra("MED", "Medicamentos y vitaminas", FaseConcepto.AMBAS, "Costo directo",
            "Compras generales"),
        ConceptoCompra("ADM", "Gastos administrativos", FaseConcepto.NA, "Gasto administrativo",
            "Compras generales"),
    )

    @Test
    fun insertarYObtenerPorCodigo() = runBlocking {
        val concepto = ConceptoCompra(
            codigo = "TRA",
            nombre = "Transporte y fletes",
            fase = FaseConcepto.AMBAS,
            naturaleza = "Costo indirecto",
            retencionSugerida = "Transporte de carga"
        )

        conceptoCompraDao.insert(concepto)
        val recuperado = conceptoCompraDao.getByCodigo("TRA").first()

        assertNotNull(recuperado)
        assertEquals("TRA", recuperado?.codigo)
        assertEquals("Transporte y fletes", recuperado?.nombre)
        assertEquals(FaseConcepto.AMBAS, recuperado?.fase)
        assertEquals("Costo indirecto", recuperado?.naturaleza)
        assertEquals("Transporte de carga", recuperado?.retencionSugerida)
    }

    @Test
    fun insertarAllYContarConceptos() = runBlocking {
        conceptoCompraDao.insertAll(conceptosBase())
        val count = conceptoCompraDao.count()
        assertEquals(5, count)
    }

    @Test
    fun filtrarPorFaseLevante() = runBlocking {
        conceptoCompraDao.insertAll(conceptosBase())
        // getPorFase LEVANTE debe devolver LEVANTE + AMBAS
        val conceptos = conceptoCompraDao.getPorFase(FaseConcepto.LEVANTE).first()
        // POL, ALI-LEV son LEVANTE; MED es AMBAS => 3
        assertEquals(3, conceptos.size)
    }

    @Test
    fun filtrarPorFaseProduccion() = runBlocking {
        conceptoCompraDao.insertAll(conceptosBase())
        // ALI-PRO es PRODUCCION; MED es AMBAS => 2
        val conceptos = conceptoCompraDao.getPorFase(FaseConcepto.PRODUCCION).first()
        assertEquals(2, conceptos.size)
    }

    @Test
    fun actualizarConcepto() = runBlocking {
        val original = ConceptoCompra("VET", "Servicios vet", FaseConcepto.AMBAS,
            "Costo", "Honorarios")
        conceptoCompraDao.insert(original)

        val actualizado = original.copy(nombre = "Servicios veterinarios especializados")
        conceptoCompraDao.update(actualizado)

        val recuperado = conceptoCompraDao.getByCodigo("VET").first()
        assertEquals("Servicios veterinarios especializados", recuperado?.nombre)
    }

    @Test
    fun insertOrReplaceEnConflicto() = runBlocking {
        val v1 = ConceptoCompra("OTR", "Otros v1", FaseConcepto.AMBAS, "Gasto", "Compras generales")
        val v2 = ConceptoCompra("OTR", "Otros v2", FaseConcepto.NA, "Gasto adm", "Sin retención")

        conceptoCompraDao.insert(v1)
        conceptoCompraDao.insert(v2) // REPLACE → reemplaza

        val recuperado = conceptoCompraDao.getByCodigo("OTR").first()
        assertEquals("Otros v2", recuperado?.nombre)
        assertEquals(FaseConcepto.NA, recuperado?.fase)
    }
}
