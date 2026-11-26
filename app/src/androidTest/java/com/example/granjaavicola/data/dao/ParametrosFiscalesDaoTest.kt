package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.ParametrosFiscales
import com.example.granjaavicola.data.entity.RegimenTributario
import com.example.granjaavicola.data.entity.TipoPersona
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

@RunWith(AndroidJUnit4::class)
class ParametrosFiscalesDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var parametrosFiscalesDao: ParametrosFiscalesDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        parametrosFiscalesDao = db.parametrosFiscalesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun tablaVaciaDevuelveNull() = runBlocking {
        val resultado = parametrosFiscalesDao.getParametros().first()
        assertNull(resultado)
    }

    @Test
    fun insertarYObtenerParametros() = runBlocking {
        val params = ParametrosFiscales(
            id = 1,
            razonSocial = "Granja Avícola San José SAS",
            nit = "900987654",
            dv = "3",
            anioGravable = 2026,
            tipoPersona = TipoPersona.JURIDICA,
            regimen = RegimenTributario.ORDINARIO,
            responsableIva = true,
            agenteRetenedor = true,
            municipio = "Floridablanca",
            ciiu = "0145",
            uvt = 52_374L,
            tarifaGmf = 0.004,
            tasaSaludEmpleado = 0.04,
            tasaPensionEmpleado = 0.04,
            tasaPensionEmpleador = 0.12,
            tasaCaja = 0.04,
            tasaCesantias = 0.0833,
            tasaInteresesCesantias = 0.01,
            tasaPrima = 0.0833,
            tasaVacaciones = 0.0417,
            reteIcaCompras = 0.0
        )

        parametrosFiscalesDao.insert(params)
        val recuperado = parametrosFiscalesDao.getParametros().first()

        assertNotNull(recuperado)
        assertEquals(1, recuperado?.id)
        assertEquals("Granja Avícola San José SAS", recuperado?.razonSocial)
        assertEquals("900987654", recuperado?.nit)
        assertEquals("3", recuperado?.dv)
        assertEquals(2026, recuperado?.anioGravable)
        assertEquals(TipoPersona.JURIDICA, recuperado?.tipoPersona)
        assertEquals(RegimenTributario.ORDINARIO, recuperado?.regimen)
        assertEquals(true, recuperado?.responsableIva)
        assertEquals(true, recuperado?.agenteRetenedor)
        assertEquals("0145", recuperado?.ciiu)
        assertEquals(52_374L, recuperado?.uvt)
        assertEquals(0.004, recuperado?.tarifaGmf ?: 0.0, 0.00001)
        assertEquals(0.04, recuperado?.tasaSaludEmpleado ?: 0.0, 0.00001)
        assertEquals(0.12, recuperado?.tasaPensionEmpleador ?: 0.0, 0.00001)
        assertEquals(0.0833, recuperado?.tasaCesantias ?: 0.0, 0.00001)
        assertEquals(0.0417, recuperado?.tasaVacaciones ?: 0.0, 0.00001)
        assertEquals(0.0, recuperado?.reteIcaCompras ?: 0.0, 0.00001)
    }

    @Test
    fun actualizarParametros() = runBlocking {
        val params = ParametrosFiscales(id = 1, nit = "111222333", uvt = 52_374L)
        parametrosFiscalesDao.insert(params)

        val actualizado = params.copy(uvt = 55_000L, anioGravable = 2027)
        parametrosFiscalesDao.update(actualizado)

        val recuperado = parametrosFiscalesDao.getParametrosDirecto()
        assertEquals(55_000L, recuperado?.uvt)
        assertEquals(2027, recuperado?.anioGravable)
    }

    @Test
    fun soloExisteUnaFila() = runBlocking {
        // Insertar dos veces con id=1 (REPLACE) → sólo queda una
        parametrosFiscalesDao.insert(ParametrosFiscales(id = 1, nit = "PRIMERA"))
        parametrosFiscalesDao.insert(ParametrosFiscales(id = 1, nit = "SEGUNDA"))
        assertEquals(1, parametrosFiscalesDao.count())
        val recuperado = parametrosFiscalesDao.getParametrosDirecto()
        assertEquals("SEGUNDA", recuperado?.nit)
    }

    @Test
    fun valoresPorDefecto() = runBlocking {
        parametrosFiscalesDao.insert(ParametrosFiscales(id = 1))
        val recuperado = parametrosFiscalesDao.getParametrosDirecto()

        assertNotNull(recuperado)
        assertEquals(52_374L, recuperado?.uvt)
        assertEquals(0.004, recuperado?.tarifaGmf ?: 0.0, 0.00001)
        assertEquals(0.04, recuperado?.tasaSaludEmpleado ?: 0.0, 0.00001)
        assertEquals(0.04, recuperado?.tasaPensionEmpleado ?: 0.0, 0.00001)
        assertEquals(0.12, recuperado?.tasaPensionEmpleador ?: 0.0, 0.00001)
        assertEquals(0.04, recuperado?.tasaCaja ?: 0.0, 0.00001)
        assertEquals(0.0833, recuperado?.tasaCesantias ?: 0.0, 0.00001)
        assertEquals(0.01, recuperado?.tasaInteresesCesantias ?: 0.0, 0.00001)
        assertEquals(0.0833, recuperado?.tasaPrima ?: 0.0, 0.00001)
        assertEquals(0.0417, recuperado?.tasaVacaciones ?: 0.0, 0.00001)
        assertEquals(0.0, recuperado?.reteIcaCompras ?: 0.0, 0.00001)
    }
}
