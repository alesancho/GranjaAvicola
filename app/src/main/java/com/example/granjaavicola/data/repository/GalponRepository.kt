package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.GalponDao
import com.example.granjaavicola.data.entity.Galpon
import kotlinx.coroutines.flow.Flow

class GalponRepository(private val galponDao: GalponDao) {

    val allGalpones: Flow<List<Galpon>> = galponDao.getAll()

    fun getById(id: Long): Flow<Galpon?> = galponDao.getById(id)

    suspend fun insert(galpon: Galpon): Long = galponDao.insert(galpon)

    suspend fun insertAll(galpones: List<Galpon>): List<Long> = galponDao.insertAll(galpones)

    suspend fun update(galpon: Galpon) = galponDao.update(galpon)

    suspend fun delete(galpon: Galpon) = galponDao.delete(galpon)
}
