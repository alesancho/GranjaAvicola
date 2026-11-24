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
import com.example.granjaavicola.data.entity.RegistroDiario
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
class RegistroDiarioDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var galponDao: GalponDao
    private lateinit var loteDao: LoteDao
    private lateinit var registroDiarioDao: RegistroDiarioDao

    private var loteId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        galponDao = db.galponDao()
        loteDao = db.loteDao()
        registroDiarioDao = db.registroDiarioDao()

        val galponId = galponDao.insert(Galpon(nombre = "Galpón 1", capacidad = 5000))
        loteId = loteDao.insert(
            Lote(
                numeroLote = "LOTE-RD-01",
                galponId = galponId,
                fechaNacimiento = LocalDate.of(2026, 1, 1),
                fechaLlegada = LocalDate.of(2026, 1, 2),
                avesIniciales = 5000,
                pesoInicialG = 42.0,
                lineaGenetica = "Hy-Line Brown",
                proveedor = "Proveedor",
                costoPorPollita = 3500.0,
                estado = EstadoLote.LEVANTE
            )
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarYObtenerRegistroDiario() = runBlocking {
        val fecha = LocalDate.of(2026, 1, 3)
        val registro = RegistroDiario(
            loteId = loteId,
            fecha = fecha,
            consumoKg = 150.5,
            aguaLitros = 300.0,
            mortalidad = 2,
            descarte = 1,
            temperaturaMax = 32.5,
            temperaturaMin = 28.0,
            horasLuz = 16.0,
            observaciones = "Aves en buen estado"
        )

        val id = registroDiarioDao.insert(registro)
        val recuperado = registroDiarioDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals(loteId, recuperado?.loteId)
        assertEquals(fecha, recuperado?.fecha)
        assertEquals(150.5, recuperado?.consumoKg ?: 0.0, 0.001)
        assertEquals(2, recuperado?.mortalidad)
        assertEquals("Aves en buen estado", recuperado?.observaciones)
    }

    @Test
    fun registrosOrdenadosPorFechaAscendente() = runBlocking {
        val r1 = RegistroDiario(
            loteId = loteId,
            fecha = LocalDate.of(2026, 1, 5),
            consumoKg = 160.0,
            aguaLitros = 320.0,
            mortalidad = 1,
            descarte = 0,
            temperaturaMax = 31.0,
            temperaturaMin = 27.5,
            horasLuz = 16.0
        )
        val r2 = RegistroDiario(
            loteId = loteId,
            fecha = LocalDate.of(2026, 1, 3),
            consumoKg = 150.0,
            aguaLitros = 300.0,
            mortalidad = 0,
            descarte = 0,
            temperaturaMax = 32.0,
            temperaturaMin = 28.0,
            horasLuz = 16.0
        )
        val r3 = RegistroDiario(
            loteId = loteId,
            fecha = LocalDate.of(2026, 1, 4),
            consumoKg = 155.0,
            aguaLitros = 310.0,
            mortalidad = 2,
            descarte = 1,
            temperaturaMax = 31.5,
            temperaturaMin = 27.8,
            horasLuz = 16.0
        )

        registroDiarioDao.insert(r1)
        registroDiarioDao.insert(r2)
        registroDiarioDao.insert(r3)

        val lista = registroDiarioDao.getRegistrosPorLote(loteId).first()
        assertEquals(3, lista.size)
        assertEquals(LocalDate.of(2026, 1, 3), lista[0].fecha)
        assertEquals(LocalDate.of(2026, 1, 4), lista[1].fecha)
        assertEquals(LocalDate.of(2026, 1, 5), lista[2].fecha)
    }

    @Test
    fun restriccionUnicidadPorLoteYFecha() = runBlocking {
        val fecha = LocalDate.of(2026, 1, 10)
        val r1 = RegistroDiario(
            loteId = loteId,
            fecha = fecha,
            consumoKg = 150.0,
            aguaLitros = 300.0,
            mortalidad = 1,
            descarte = 0,
            temperaturaMax = 30.0,
            temperaturaMin = 25.0,
            horasLuz = 16.0
        )
        val r2 = RegistroDiario(
            loteId = loteId,
            fecha = fecha,
            consumoKg = 155.0,
            aguaLitros = 310.0,
            mortalidad = 0,
            descarte = 0,
            temperaturaMax = 30.0,
            temperaturaMin = 25.0,
            horasLuz = 16.0
        )

        registroDiarioDao.insert(r1)

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                registroDiarioDao.insert(r2)
            }
        }
    }

    @Test
    fun borrarRegistroDiario() = runBlocking {
        val registro = RegistroDiario(
            loteId = loteId,
            fecha = LocalDate.of(2026, 1, 12),
            consumoKg = 150.0,
            aguaLitros = 300.0,
            mortalidad = 0,
            descarte = 0,
            temperaturaMax = 30.0,
            temperaturaMin = 25.0,
            horasLuz = 16.0
        )

        val id = registroDiarioDao.insert(registro)
        val recuperado = registroDiarioDao.getById(id).first()!!

        registroDiarioDao.delete(recuperado)

        val despuesDeBorrar = registroDiarioDao.getById(id).first()
        assertNull(despuesDeBorrar)
    }
}
