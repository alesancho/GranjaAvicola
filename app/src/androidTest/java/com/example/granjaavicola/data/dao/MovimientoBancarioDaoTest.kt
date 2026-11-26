package com.example.granjaavicola.data.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.MedioMovimientoBancario
import com.example.granjaavicola.data.entity.MovimientoBancario
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoDocumento
import com.example.granjaavicola.data.entity.TipoMovimientoBancario
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
class MovimientoBancarioDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var terceroDao: TerceroDao
    private lateinit var movimientoBancarioDao: MovimientoBancarioDao

    private var terceroId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        terceroDao = db.terceroDao()
        movimientoBancarioDao = db.movimientoBancarioDao()

        terceroId = terceroDao.insert(
            Tercero(
                nombre = "Distribuidora de Concentrados del Oriente SAS",
                tipoDocumento = TipoDocumento.NIT,
                documento = "900555666",
                digitoVerificacion = 3,
                tipo = TipoTercero.PROVEEDOR,
                telefono = "3101234567",
                direccion = "Zona Industrial Km 4",
                correo = "facturacion@distribuidora.com",
                ciudad = "Bucaramanga",
                responsableIva = true,
                obligadoFacturar = true
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerMovimientoBancarioPorId() = runBlocking {
        val fecha = LocalDate.of(2026, 3, 15)
        val movimiento = MovimientoBancario(
            fecha = fecha,
            descripcion = "Pago transferencia factura compra concentrado",
            valor = 5_250_000L,
            tipo = TipoMovimientoBancario.EGRESO,
            banco = "Bancolombia",
            cuenta = "Bancolombia Ahorros 123-456789-01",
            medio = MedioMovimientoBancario.TRANSFERENCIA,
            terceroId = terceroId,
            sujetoGmf = true,
            gmfEstimado = 21_000L,
            soporte = "Comprobante-2026-03-15",
            conciliado = true
        )

        val id = movimientoBancarioDao.insert(movimiento)
        val recuperado = movimientoBancarioDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals(fecha, recuperado?.fecha)
        assertEquals("Pago transferencia factura compra concentrado", recuperado?.descripcion)
        assertEquals(5_250_000L, recuperado?.valor)
        assertEquals(TipoMovimientoBancario.EGRESO, recuperado?.tipo)
        assertEquals("Bancolombia", recuperado?.banco)
        assertEquals("Bancolombia Ahorros 123-456789-01", recuperado?.cuenta)
        assertEquals(MedioMovimientoBancario.TRANSFERENCIA, recuperado?.medio)
        assertEquals(terceroId, recuperado?.terceroId)
        assertEquals(true, recuperado?.sujetoGmf)
        assertEquals(21_000L, recuperado?.gmfEstimado)
        assertEquals("Comprobante-2026-03-15", recuperado?.soporte)
        assertEquals(true, recuperado?.conciliado)
    }

    @Test
    fun insertarMovimientoBancarioSinTerceroYListarPorCuenta() = runBlocking {
        val mov = MovimientoBancario(
            fecha = LocalDate.of(2026, 3, 16),
            descripcion = "Abono cliente contado consignación",
            valor = 1_800_000L,
            tipo = TipoMovimientoBancario.INGRESO,
            banco = "Davivienda",
            cuenta = "Davivienda 987-654321-00",
            medio = MedioMovimientoBancario.EFECTIVO,
            terceroId = null,
            sujetoGmf = false,
            gmfEstimado = 0L,
            conciliado = false
        )

        movimientoBancarioDao.insert(mov)
        val lista = movimientoBancarioDao.getPorCuenta("Davivienda 987-654321-00").first()

        assertEquals(1, lista.size)
        assertEquals(1_800_000L, lista[0].valor)
        assertEquals(TipoMovimientoBancario.INGRESO, lista[0].tipo)
        assertEquals(MedioMovimientoBancario.EFECTIVO, lista[0].medio)
        assertEquals(false, lista[0].sujetoGmf)
        assertEquals(false, lista[0].conciliado)
    }

    @Test
    fun restriccionForeignKeyTerceroAlEliminarTerceroConMovimiento() = runBlocking {
        val movimiento = MovimientoBancario(
            fecha = LocalDate.of(2026, 3, 15),
            descripcion = "Pago a proveedor",
            valor = 500_000L,
            tipo = TipoMovimientoBancario.EGRESO,
            banco = "Bancolombia",
            cuenta = "Bancolombia",
            medio = MedioMovimientoBancario.CHEQUE,
            terceroId = terceroId,
            conciliado = false
        )
        movimientoBancarioDao.insert(movimiento)

        val tercero = terceroDao.getById(terceroId).first()
        assertNotNull(tercero)

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                terceroDao.delete(tercero!!)
            }
        }
    }
}
