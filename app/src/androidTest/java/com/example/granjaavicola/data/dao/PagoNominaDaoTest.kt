package com.example.granjaavicola.data.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.Empleado
import com.example.granjaavicola.data.entity.PagoNomina
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
class PagoNominaDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var empleadoDao: EmpleadoDao
    private lateinit var pagoNominaDao: PagoNominaDao

    private var empleadoId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        empleadoDao = db.empleadoDao()
        pagoNominaDao = db.pagoNominaDao()

        empleadoId = empleadoDao.insert(
            Empleado(
                nombre = "Juan Pablo Rincón",
                documento = "1098555444",
                cargo = "Operario de Galpón",
                salarioBase = 1_423_500L,
                fechaIngreso = LocalDate.of(2025, 2, 1),
                activo = true
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerPagoNominaPorId() = runBlocking {
        val pago = PagoNomina(
            empleadoId = empleadoId,
            periodo = "2026-02",
            salario = 1_423_500L,
            auxilioTransporte = 162_000L,
            deducciones = 113_880L,
            netoPagado = 1_471_620L
        )

        val id = pagoNominaDao.insert(pago)
        val recuperado = pagoNominaDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals(empleadoId, recuperado?.empleadoId)
        assertEquals("2026-02", recuperado?.periodo)
        assertEquals(1_423_500L, recuperado?.salario)
        assertEquals(162_000L, recuperado?.auxilioTransporte)
        assertEquals(113_880L, recuperado?.deducciones)
        assertEquals(1_471_620L, recuperado?.netoPagado)
    }

    @Test
    fun listarPagosNominaPorPeriodo() = runBlocking {
        val pago1 = PagoNomina(
            empleadoId = empleadoId,
            periodo = "2026-01",
            salario = 1_423_500L,
            auxilioTransporte = 162_000L,
            deducciones = 113_880L,
            netoPagado = 1_471_620L
        )
        val pago2 = PagoNomina(
            empleadoId = empleadoId,
            periodo = "2026-02",
            salario = 1_423_500L,
            auxilioTransporte = 162_000L,
            deducciones = 113_880L,
            netoPagado = 1_471_620L
        )

        pagoNominaDao.insert(pago1)
        pagoNominaDao.insert(pago2)

        val lista = pagoNominaDao.getPorPeriodo("2026-02").first()
        assertEquals(1, lista.size)
        assertEquals("2026-02", lista[0].periodo)
        assertEquals(empleadoId, lista[0].empleadoId)
    }

    @Test
    fun restriccionForeignKeyEmpleadoAlEliminarConPagos() = runBlocking {
        val pago = PagoNomina(
            empleadoId = empleadoId,
            periodo = "2026-01",
            salario = 1_423_500L,
            auxilioTransporte = 162_000L,
            deducciones = 113_880L,
            netoPagado = 1_471_620L
        )
        pagoNominaDao.insert(pago)

        val empleado = empleadoDao.getById(empleadoId).first()
        assertNotNull(empleado)

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                empleadoDao.delete(empleado!!)
            }
        }
    }
}
