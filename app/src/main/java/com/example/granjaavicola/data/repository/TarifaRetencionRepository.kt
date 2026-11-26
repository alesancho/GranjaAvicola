package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.TarifaRetencionDao
import com.example.granjaavicola.data.entity.TarifaRetencion
import kotlinx.coroutines.flow.Flow

class TarifaRetencionRepository(private val tarifaRetencionDao: TarifaRetencionDao) {

    val allTarifas: Flow<List<TarifaRetencion>> = tarifaRetencionDao.getAll()

    fun getByConcepto(concepto: String): Flow<TarifaRetencion?> =
        tarifaRetencionDao.getByConcepto(concepto)

    suspend fun count(): Int = tarifaRetencionDao.count()

    suspend fun insert(tarifa: TarifaRetencion): Long = tarifaRetencionDao.insert(tarifa)

    suspend fun insertAll(tarifas: List<TarifaRetencion>): List<Long> =
        tarifaRetencionDao.insertAll(tarifas)

    suspend fun update(tarifa: TarifaRetencion) = tarifaRetencionDao.update(tarifa)

    suspend fun delete(tarifa: TarifaRetencion) = tarifaRetencionDao.delete(tarifa)
}
