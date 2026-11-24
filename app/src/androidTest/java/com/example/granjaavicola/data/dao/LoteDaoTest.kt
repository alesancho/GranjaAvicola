package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Galpon
import com.example.granjaavicola.data.entity.Lote
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class LoteDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var galponDao: GalponDao
    private lateinit var loteDao: LoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        galponDao = db.galponDao()
        loteDao = db.loteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerLotePorId() = runBlocking {
        val galponId = galponDao.insert(
            Galpon(nombre = "Galpón 1", capacidad = 5000)
        )

        val lote = Lote(
            numeroLote = "LOTE-001",
            galponId = galponId,
            fechaNacimiento = LocalDate.of(2026, 1, 1),
            fechaLlegada = LocalDate.of(2026, 1, 2),
            avesIniciales = 4800,
            pesoInicialG = 42.5,
            lineaGenetica = "Hy-Line Brown",
            proveedor = "Incubadora Central",
            costoPorPollita = 3500.0,
            estado = EstadoLote.LEVANTE
        )

        val loteId = loteDao.insert(lote)
        val recuperado = loteDao.getById(loteId).first()

        assertNotNull(recuperado)
        assertEquals("LOTE-001", recuperado?.numeroLote)
        assertEquals(galponId, recuperado?.galponId)
        assertEquals(EstadoLote.LEVANTE, recuperado?.estado)
        assertEquals(4800, recuperado?.avesIniciales)
    }

    @Test
    fun obtenerLotesActivos() = runBlocking {
        val galponId = galponDao.insert(
            Galpon(nombre = "Galpón 2", capacidad = 3000)
        )

        val loteLevante = Lote(
            numeroLote = "L-LEV",
            galponId = galponId,
            fechaNacimiento = LocalDate.of(2026, 1, 1),
            fechaLlegada = LocalDate.of(2026, 1, 2),
            avesIniciales = 2000,
            pesoInicialG = 40.0,
            lineaGenetica = "Lohmann Brown",
            proveedor = "Prov A",
            costoPorPollita = 3200.0,
            estado = EstadoLote.LEVANTE
        )
        val loteProduccion = Lote(
            numeroLote = "L-PROD",
            galponId = galponId,
            fechaNacimiento = LocalDate.of(2025, 8, 1),
            fechaLlegada = LocalDate.of(2025, 8, 2),
            avesIniciales = 2000,
            pesoInicialG = 41.0,
            lineaGenetica = "Lohmann Brown",
            proveedor = "Prov A",
            costoPorPollita = 3200.0,
            estado = EstadoLote.PRODUCCION
        )
        val loteCerrado = Lote(
            numeroLote = "L-CERR",
            galponId = galponId,
            fechaNacimiento = LocalDate.of(2024, 1, 1),
            fechaLlegada = LocalDate.of(2024, 1, 2),
            avesIniciales = 2000,
            pesoInicialG = 41.0,
            lineaGenetica = "Lohmann Brown",
            proveedor = "Prov A",
            costoPorPollita = 3000.0,
            estado = EstadoLote.CERRADO,
            fechaCierre = LocalDate.of(2025, 12, 31)
        )

        loteDao.insert(loteLevante)
        loteDao.insert(loteProduccion)
        loteDao.insert(loteCerrado)

        val activos = loteDao.getLotesActivos().first()
        assertEquals(2, activos.size)
        val numeros = activos.map { it.numeroLote }
        assert(numeros.contains("L-LEV"))
        assert(numeros.contains("L-PROD"))
        assert(!numeros.contains("L-CERR"))
    }

    @Test
    fun actualizarLote() = runBlocking {
        val galponId = galponDao.insert(Galpon(nombre = "Galpón 3", capacidad = 4000))
        val loteId = loteDao.insert(
            Lote(
                numeroLote = "L-UPD",
                galponId = galponId,
                fechaNacimiento = LocalDate.of(2026, 1, 1),
                fechaLlegada = LocalDate.of(2026, 1, 2),
                avesIniciales = 3000,
                pesoInicialG = 42.0,
                lineaGenetica = "Hy-Line Brown",
                proveedor = "Prov B",
                costoPorPollita = 3400.0,
                estado = EstadoLote.LEVANTE
            )
        )

        val loteExistente = loteDao.getById(loteId).first()!!
        val loteActualizado = loteExistente.copy(
            estado = EstadoLote.PRODUCCION,
            fechaTraslado = LocalDate.of(2026, 5, 1)
        )
        loteDao.update(loteActualizado)

        val resultado = loteDao.getById(loteId).first()
        assertEquals(EstadoLote.PRODUCCION, resultado?.estado)
        assertEquals(LocalDate.of(2026, 5, 1), resultado?.fechaTraslado)
    }

    @Test
    fun borrarLote() = runBlocking {
        val galponId = galponDao.insert(Galpon(nombre = "Galpón 4", capacidad = 4000))
        val loteId = loteDao.insert(
            Lote(
                numeroLote = "L-DEL",
                galponId = galponId,
                fechaNacimiento = LocalDate.of(2026, 1, 1),
                fechaLlegada = LocalDate.of(2026, 1, 2),
                avesIniciales = 1000,
                pesoInicialG = 40.0,
                lineaGenetica = "Hy-Line Brown",
                proveedor = "Prov C",
                costoPorPollita = 3300.0,
                estado = EstadoLote.LEVANTE
            )
        )

        val lote = loteDao.getById(loteId).first()!!
        loteDao.delete(lote)

        val resultado = loteDao.getById(loteId).first()
        assertNull(resultado)
    }
}
