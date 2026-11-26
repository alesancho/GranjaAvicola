package com.example.granjaavicola.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.granjaavicola.data.db.AppDatabase
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoDocumento
import com.example.granjaavicola.data.entity.TipoTercero
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
class TerceroDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var terceroDao: TerceroDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        terceroDao = db.terceroDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertarPersonaNaturalConCamposDian() = runBlocking {
        val tercero = Tercero(
            nombre = "Rodríguez González Carlos Andrés",
            primerApellido = "Rodríguez",
            segundoApellido = "González",
            primerNombre = "Carlos",
            otrosNombres = "Andrés",
            tipoDocumento = TipoDocumento.CC,
            documento = "1098765432",
            tipo = TipoTercero.PROVEEDOR,
            codDepartamento = "68",
            codMunicipio = "68001",
            codPais = "169",
            responsableIva = false,
            obligadoFacturar = false
        )

        val id = terceroDao.insert(tercero)
        val recuperado = terceroDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals("Rodríguez", recuperado?.primerApellido)
        assertEquals("González", recuperado?.segundoApellido)
        assertEquals("Carlos", recuperado?.primerNombre)
        assertEquals("Andrés", recuperado?.otrosNombres)
        assertEquals(TipoDocumento.CC, recuperado?.tipoDocumento)
        assertEquals("13", recuperado?.tipoDocumento?.codigoDian)
        assertEquals("68", recuperado?.codDepartamento)
        assertEquals("68001", recuperado?.codMunicipio)
        assertEquals("169", recuperado?.codPais)
        assertEquals(false, recuperado?.responsableIva)
        assertEquals(false, recuperado?.obligadoFacturar)
    }

    @Test
    fun insertarPersonaJuridicaConNit() = runBlocking {
        val tercero = Tercero(
            nombre = "Avícola del Norte SAS",
            tipoDocumento = TipoDocumento.NIT,
            documento = "900111222",
            digitoVerificacion = 5,
            tipo = TipoTercero.CLIENTE,
            codDepartamento = "05",
            codMunicipio = "05001",
            codPais = "169",
            responsableIva = true,
            obligadoFacturar = true
        )

        val id = terceroDao.insert(tercero)
        val recuperado = terceroDao.getById(id).first()

        assertNotNull(recuperado)
        assertEquals("31", recuperado?.tipoDocumento?.codigoDian)
        assertEquals(5, recuperado?.digitoVerificacion)
        assertEquals(true, recuperado?.responsableIva)
        assertEquals(true, recuperado?.obligadoFacturar)
    }

    @Test
    fun codigosDianMapeadosCorrectamente() {
        assertEquals("13", TipoDocumento.CC.codigoDian)
        assertEquals("31", TipoDocumento.NIT.codigoDian)
        assertEquals("22", TipoDocumento.CE.codigoDian)
        assertEquals("41", TipoDocumento.PASAPORTE.codigoDian)
        assertEquals("43", TipoDocumento.OTRO.codigoDian)
        assertEquals(TipoDocumento.CC, TipoDocumento.fromCodigoDian("13"))
        assertEquals(TipoDocumento.NIT, TipoDocumento.fromCodigoDian("31"))
    }

    @Test
    fun camposOpcionalesToleranNull() = runBlocking {
        val tercero = Tercero(
            nombre = "Proveedor Mínimo",
            tipoDocumento = TipoDocumento.OTRO,
            documento = "SIN-ID",
            tipo = TipoTercero.PROVEEDOR
        )

        val id = terceroDao.insert(tercero)
        val recuperado = terceroDao.getById(id).first()

        assertNotNull(recuperado)
        assertNull(recuperado?.primerApellido)
        assertNull(recuperado?.segundoApellido)
        assertNull(recuperado?.primerNombre)
        assertNull(recuperado?.otrosNombres)
        assertNull(recuperado?.codDepartamento)
        assertNull(recuperado?.codMunicipio)
        assertEquals("169", recuperado?.codPais)
    }

    @Test
    fun listarProveedoresYClientes() = runBlocking {
        terceroDao.insert(Tercero(nombre = "P1", documento = "111", tipo = TipoTercero.PROVEEDOR))
        terceroDao.insert(Tercero(nombre = "C1", documento = "222", tipo = TipoTercero.CLIENTE))
        terceroDao.insert(Tercero(nombre = "A1", documento = "333", tipo = TipoTercero.AMBOS))

        val proveedores = terceroDao.getProveedores().first()
        val clientes = terceroDao.getClientes().first()

        assertEquals(2, proveedores.size) // PROVEEDOR + AMBOS
        assertEquals(2, clientes.size)    // CLIENTE + AMBOS
    }
}
