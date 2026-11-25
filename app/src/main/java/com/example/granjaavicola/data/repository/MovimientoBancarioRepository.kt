package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.MovimientoBancarioDao
import com.example.granjaavicola.data.entity.MovimientoBancario
import com.example.granjaavicola.data.entity.TipoMovimientoBancario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class MovimientoBancarioRepository(private val movimientoBancarioDao: MovimientoBancarioDao) {

    val allMovimientos: Flow<List<MovimientoBancario>> = movimientoBancarioDao.getAll()

    fun getById(id: Long): Flow<MovimientoBancario?> = movimientoBancarioDao.getById(id)

    fun getPorCuenta(cuenta: String): Flow<List<MovimientoBancario>> =
        movimientoBancarioDao.getPorCuenta(cuenta)

    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<MovimientoBancario>> =
        movimientoBancarioDao.getPorRangoFecha(fechaInicio, fechaFin)

    fun getPorTipo(tipo: TipoMovimientoBancario): Flow<List<MovimientoBancario>> =
        movimientoBancarioDao.getPorTipo(tipo)

    fun getPorConciliado(conciliado: Boolean): Flow<List<MovimientoBancario>> =
        movimientoBancarioDao.getPorConciliado(conciliado)

    fun getPorTercero(terceroId: Long): Flow<List<MovimientoBancario>> =
        movimientoBancarioDao.getPorTercero(terceroId)

    suspend fun insert(movimiento: MovimientoBancario): Long =
        movimientoBancarioDao.insert(movimiento)

    suspend fun insertAll(movimientos: List<MovimientoBancario>): List<Long> =
        movimientoBancarioDao.insertAll(movimientos)

    suspend fun update(movimiento: MovimientoBancario) =
        movimientoBancarioDao.update(movimiento)

    suspend fun delete(movimiento: MovimientoBancario) =
        movimientoBancarioDao.delete(movimiento)
}
