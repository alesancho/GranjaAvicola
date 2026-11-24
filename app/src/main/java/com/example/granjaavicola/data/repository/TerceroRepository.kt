package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.TerceroDao
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoTercero
import kotlinx.coroutines.flow.Flow

class TerceroRepository(private val terceroDao: TerceroDao) {

    val allTerceros: Flow<List<Tercero>> = terceroDao.getAll()

    val clientes: Flow<List<Tercero>> = terceroDao.getClientes()

    val proveedores: Flow<List<Tercero>> = terceroDao.getProveedores()

    fun getById(id: Long): Flow<Tercero?> = terceroDao.getById(id)

    fun getPorTipo(tipo: TipoTercero): Flow<List<Tercero>> = terceroDao.getPorTipo(tipo)

    fun getPorDocumento(documento: String): Flow<Tercero?> = terceroDao.getPorDocumento(documento)

    suspend fun insert(tercero: Tercero): Long = terceroDao.insert(tercero)

    suspend fun insertAll(terceros: List<Tercero>): List<Long> = terceroDao.insertAll(terceros)

    suspend fun update(tercero: Tercero) = terceroDao.update(tercero)

    suspend fun delete(tercero: Tercero) = terceroDao.delete(tercero)
}
