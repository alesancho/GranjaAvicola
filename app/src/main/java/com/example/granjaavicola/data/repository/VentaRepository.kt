package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.VentaDao
import com.example.granjaavicola.data.entity.ProductoVenta
import com.example.granjaavicola.data.entity.Venta
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class VentaRepository(private val ventaDao: VentaDao) {

    val allVentas: Flow<List<Venta>> = ventaDao.getAll()

    fun getById(id: Long): Flow<Venta?> = ventaDao.getById(id)

    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Venta>> =
        ventaDao.getPorRangoFecha(fechaInicio, fechaFin)

    fun getPorTercero(terceroId: Long): Flow<List<Venta>> = ventaDao.getPorTercero(terceroId)

    fun getPorLote(loteId: Long): Flow<List<Venta>> = ventaDao.getPorLote(loteId)

    fun getPorProducto(producto: ProductoVenta): Flow<List<Venta>> =
        ventaDao.getPorProducto(producto)

    suspend fun insert(venta: Venta): Long = ventaDao.insert(venta)

    suspend fun insertAll(ventas: List<Venta>): List<Long> = ventaDao.insertAll(ventas)

    suspend fun update(venta: Venta) = ventaDao.update(venta)

    suspend fun delete(venta: Venta) = ventaDao.delete(venta)
}
