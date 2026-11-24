package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.InventarioAlimentoMovimientoDao
import com.example.granjaavicola.data.entity.InventarioAlimentoMovimiento
import com.example.granjaavicola.data.entity.TipoMovimientoInventario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class InventarioAlimentoMovimientoRepository(
    private val inventarioAlimentoMovimientoDao: InventarioAlimentoMovimientoDao
) {

    val allMovimientos: Flow<List<InventarioAlimentoMovimiento>> =
        inventarioAlimentoMovimientoDao.getAll()

    val allMovimientosOrdenadosPorFecha: Flow<List<InventarioAlimentoMovimiento>> =
        inventarioAlimentoMovimientoDao.getAllOrdenadosPorFecha()

    fun getById(id: Long): Flow<InventarioAlimentoMovimiento?> =
        inventarioAlimentoMovimientoDao.getById(id)

    fun getPorTipo(tipo: TipoMovimientoInventario): Flow<List<InventarioAlimentoMovimiento>> =
        inventarioAlimentoMovimientoDao.getPorTipo(tipo)

    fun getPorLote(loteId: Long): Flow<List<InventarioAlimentoMovimiento>> =
        inventarioAlimentoMovimientoDao.getPorLote(loteId)

    fun getPorRangoFecha(
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<InventarioAlimentoMovimiento>> =
        inventarioAlimentoMovimientoDao.getPorRangoFecha(fechaInicio, fechaFin)

    suspend fun insert(movimiento: InventarioAlimentoMovimiento): Long =
        inventarioAlimentoMovimientoDao.insert(movimiento)

    suspend fun insertAll(movimientos: List<InventarioAlimentoMovimiento>): List<Long> =
        inventarioAlimentoMovimientoDao.insertAll(movimientos)

    suspend fun update(movimiento: InventarioAlimentoMovimiento) =
        inventarioAlimentoMovimientoDao.update(movimiento)

    suspend fun delete(movimiento: InventarioAlimentoMovimiento) =
        inventarioAlimentoMovimientoDao.delete(movimiento)
}
