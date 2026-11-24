package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.PesajeHuevoDao
import com.example.granjaavicola.data.entity.PesajeHuevo
import kotlinx.coroutines.flow.Flow

class PesajeHuevoRepository(private val pesajeHuevoDao: PesajeHuevoDao) {

    fun getById(id: Long): Flow<PesajeHuevo?> = pesajeHuevoDao.getById(id)

    fun getPorLote(loteId: Long): Flow<List<PesajeHuevo>> =
        pesajeHuevoDao.getPesajesPorLote(loteId)

    fun getPorLoteYSemana(loteId: Long, semana: Int): Flow<List<PesajeHuevo>> =
        pesajeHuevoDao.getPesajesPorLoteYSemana(loteId, semana)

    suspend fun insert(pesaje: PesajeHuevo): Long = pesajeHuevoDao.insert(pesaje)

    suspend fun insertAll(pesajes: List<PesajeHuevo>): List<Long> =
        pesajeHuevoDao.insertAll(pesajes)

    suspend fun update(pesaje: PesajeHuevo) = pesajeHuevoDao.update(pesaje)

    suspend fun delete(pesaje: PesajeHuevo) = pesajeHuevoDao.delete(pesaje)
}
