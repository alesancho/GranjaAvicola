package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.ActivoFijoDao
import com.example.granjaavicola.data.entity.ActivoFijo
import kotlinx.coroutines.flow.Flow

class ActivoFijoRepository(private val activoFijoDao: ActivoFijoDao) {

    val allActivosFijos: Flow<List<ActivoFijo>> = activoFijoDao.getAll()

    fun getById(id: Long): Flow<ActivoFijo?> = activoFijoDao.getById(id)

    suspend fun insert(activoFijo: ActivoFijo): Long = activoFijoDao.insert(activoFijo)

    suspend fun insertAll(activosFijos: List<ActivoFijo>): List<Long> =
        activoFijoDao.insertAll(activosFijos)

    suspend fun update(activoFijo: ActivoFijo) = activoFijoDao.update(activoFijo)

    suspend fun delete(activoFijo: ActivoFijo) = activoFijoDao.delete(activoFijo)
}
