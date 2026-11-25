package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.Empleado
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
class EmpleadoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var empleadoDao: EmpleadoDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        empleadoDao = db.empleadoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerEmpleadoPorId() = runBlocking {
        val fechaIngreso = LocalDate.of(2025, 1, 15)
        val empleado = Empleado(
            nombre = "Carlos Andrés Pérez",
            documento = "1098765432",
            cargo = "Galponero Líder",
            salarioBase = 1_600_000L,
            fechaIngreso = fechaIngreso,
            activo = true
        )

        val id = empleadoDao.insert(empleado)
        val recuperado = empleadoDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals("Carlos Andrés Pérez", recuperado?.nombre)
        assertEquals("1098765432", recuperado?.documento)
        assertEquals("Galponero Líder", recuperado?.cargo)
        assertEquals(1_600_000L, recuperado?.salarioBase)
        assertEquals(fechaIngreso, recuperado?.fechaIngreso)
        assertEquals(true, recuperado?.activo)
    }

    @Test
    fun obtenerEmpleadoPorDocumentoYListarActivos() = runBlocking {
        val emp1 = Empleado(
            nombre = "María Gómez",
            documento = "1098111222",
            cargo = "Supervisora de Calidad",
            salarioBase = 2_200_000L,
            fechaIngreso = LocalDate.of(2025, 3, 1),
            activo = true
        )
        val emp2 = Empleado(
            nombre = "Pedro Duarte",
            documento = "1098333444",
            cargo = "Operario",
            salarioBase = 1_423_500L,
            fechaIngreso = LocalDate.of(2024, 6, 1),
            activo = false
        )

        empleadoDao.insert(emp1)
        empleadoDao.insert(emp2)

        val encontrado = empleadoDao.getPorDocumento("1098111222").first()
        assertNotNull(encontrado)
        assertEquals("María Gómez", encontrado?.nombre)

        val activos = empleadoDao.getActivos(true).first()
        assertEquals(1, activos.size)
        assertEquals("María Gómez", activos[0].nombre)
    }
}
