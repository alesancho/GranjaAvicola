package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.CompraDao
import com.example.granjaavicola.data.entity.CategoriaCompra
import com.example.granjaavicola.data.entity.Compra
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class CompraRepository(private val compraDao: CompraDao) {

    val allCompras: Flow<List<Compra>> = compraDao.getAll()

    fun getById(id: Long): Flow<Compra?> = compraDao.getById(id)

    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Compra>> =
        compraDao.getPorRangoFecha(fechaInicio, fechaFin)

    fun getPorTercero(terceroId: Long): Flow<List<Compra>> = compraDao.getPorTercero(terceroId)

    fun getPorLote(loteId: Long): Flow<List<Compra>> = compraDao.getPorLote(loteId)

    fun getPorCategoria(categoria: CategoriaCompra): Flow<List<Compra>> =
        compraDao.getPorCategoria(categoria)

    fun getPorEstadoPago(pagada: Boolean): Flow<List<Compra>> =
        compraDao.getPorEstadoPago(pagada)

    suspend fun insert(compra: Compra): Long = compraDao.insert(compra)

    suspend fun insertAll(compras: List<Compra>): List<Long> = compraDao.insertAll(compras)

    suspend fun update(compra: Compra) = compraDao.update(compra)

    suspend fun delete(compra: Compra) = compraDao.delete(compra)
}
