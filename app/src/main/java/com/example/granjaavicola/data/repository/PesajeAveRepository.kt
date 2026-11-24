package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.PesajeAveDao
import com.example.granjaavicola.data.entity.PesajeAve
import kotlinx.coroutines.flow.Flow

class PesajeAveRepository(private val pesajeAveDao: PesajeAveDao) {

    fun getById(id: Long): Flow<PesajeAve?> = pesajeAveDao.getById(id)

    fun getPorLote(loteId: Long): Flow<List<PesajeAve>> =
        pesajeAveDao.getPesajesPorLote(loteId)

    fun getPorLoteYSemana(loteId: Long, semana: Int): Flow<List<PesajeAve>> =
        pesajeAveDao.getPesajesPorLoteYSemana(loteId, semana)

    suspend fun insert(pesaje: PesajeAve): Long = pesajeAveDao.insert(pesaje)

    suspend fun insertAll(pesajes: List<PesajeAve>): List<Long> =
        pesajeAveDao.insertAll(pesajes)

    suspend fun update(pesaje: PesajeAve) = pesajeAveDao.update(pesaje)

    suspend fun delete(pesaje: PesajeAve) = pesajeAveDao.delete(pesaje)
}
