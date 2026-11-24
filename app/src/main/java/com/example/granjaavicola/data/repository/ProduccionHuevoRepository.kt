package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.ProduccionHuevoDao
import com.example.granjaavicola.data.entity.ProduccionHuevo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ProduccionHuevoRepository(private val produccionHuevoDao: ProduccionHuevoDao) {

    fun getById(id: Long): Flow<ProduccionHuevo?> = produccionHuevoDao.getById(id)

    fun getPorLote(loteId: Long): Flow<List<ProduccionHuevo>> =
        produccionHuevoDao.getProduccionPorLote(loteId)

    fun getPorLoteYFecha(loteId: Long, fecha: LocalDate): Flow<ProduccionHuevo?> =
        produccionHuevoDao.getProduccionPorLoteYFecha(loteId, fecha)

    fun getPorLoteYRangoFecha(
        loteId: Long,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<ProduccionHuevo>> =
        produccionHuevoDao.getProduccionPorLoteYRangoFecha(loteId, fechaInicio, fechaFin)

    suspend fun insert(produccion: ProduccionHuevo): Long = produccionHuevoDao.insert(produccion)

    suspend fun insertAll(producciones: List<ProduccionHuevo>): List<Long> =
        produccionHuevoDao.insertAll(producciones)

    suspend fun update(produccion: ProduccionHuevo) = produccionHuevoDao.update(produccion)

    suspend fun delete(produccion: ProduccionHuevo) = produccionHuevoDao.delete(produccion)
}
