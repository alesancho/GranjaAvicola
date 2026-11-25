package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.ActivoFijo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ActivoFijoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var activoFijoDao: ActivoFijoDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activoFijoDao = db.activoFijoDao()
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
            descripcion = "Planta Eléctrica Diésel 15kVA",
            fechaCompra = fechaCompra,
            valorCompra = 28_000_000L,
            vidaUtilMeses = 120,
            depreciacionAcumulada = 2_100_000L
        )

        val id = activoFijoDao.insert(activo)
        val recuperado = activoFijoDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals("Planta Eléctrica Diésel 15kVA", recuperado?.descripcion)
        assertEquals(fechaCompra, recuperado?.fechaCompra)
        assertEquals(28_000_000L, recuperado?.valorCompra)
        assertEquals(120, recuperado?.vidaUtilMeses)
        assertEquals(2_100_000L, recuperado?.depreciacionAcumulada)
    }

    @Test
    fun listarActivosFijosYActualizar() = runBlocking {
        val activo = ActivoFijo(
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
}
