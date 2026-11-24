package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.PagoDao
import com.example.granjaavicola.data.entity.Pago
import com.example.granjaavicola.data.entity.TipoPago
import com.example.granjaavicola.data.entity.TipoReferenciaPago
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class PagoRepository(private val pagoDao: PagoDao) {

    val allPagos: Flow<List<Pago>> = pagoDao.getAll()

    fun getById(id: Long): Flow<Pago?> = pagoDao.getById(id)

    fun getPorReferencia(
        referenciaId: Long,
        referenciaTipo: TipoReferenciaPago
    ): Flow<List<Pago>> = pagoDao.getPorReferencia(referenciaId, referenciaTipo)

    fun getPorTipo(tipo: TipoPago): Flow<List<Pago>> = pagoDao.getPorTipo(tipo)

    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Pago>> =
        pagoDao.getPorRangoFecha(fechaInicio, fechaFin)

    suspend fun insert(pago: Pago): Long = pagoDao.insert(pago)

    suspend fun insertAll(pagos: List<Pago>): List<Long> = pagoDao.insertAll(pagos)

    suspend fun update(pago: Pago) = pagoDao.update(pago)

    suspend fun delete(pago: Pago) = pagoDao.delete(pago)
}
