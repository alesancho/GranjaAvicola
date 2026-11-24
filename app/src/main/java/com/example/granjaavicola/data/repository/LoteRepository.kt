package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.LoteDao
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Lote
import kotlinx.coroutines.flow.Flow

class LoteRepository(private val loteDao: LoteDao) {

    val allLotes: Flow<List<Lote>> = loteDao.getAll()

    val lotesActivos: Flow<List<Lote>> = loteDao.getLotesActivos()

    fun getById(id: Long): Flow<Lote?> = loteDao.getById(id)

    fun getPorEstado(estado: EstadoLote): Flow<List<Lote>> = loteDao.getLotesPorEstado(estado)

    fun getPorGalpon(galponId: Long): Flow<List<Lote>> = loteDao.getLotesPorGalpon(galponId)

    suspend fun insert(lote: Lote): Long = loteDao.insert(lote)

    suspend fun insertAll(lotes: List<Lote>): List<Long> = loteDao.insertAll(lotes)

    suspend fun update(lote: Lote) = loteDao.update(lote)

    suspend fun delete(lote: Lote) = loteDao.delete(lote)
}
