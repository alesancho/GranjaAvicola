package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.PagoNominaDao
import com.example.granjaavicola.data.entity.PagoNomina
import kotlinx.coroutines.flow.Flow

class PagoNominaRepository(private val pagoNominaDao: PagoNominaDao) {

    val allPagosNomina: Flow<List<PagoNomina>> = pagoNominaDao.getAll()

    fun getById(id: Long): Flow<PagoNomina?> = pagoNominaDao.getById(id)

    fun getPorEmpleado(empleadoId: Long): Flow<List<PagoNomina>> =
        pagoNominaDao.getPorEmpleado(empleadoId)

    fun getPorPeriodo(periodo: String): Flow<List<PagoNomina>> =
        pagoNominaDao.getPorPeriodo(periodo)

    suspend fun insert(pagoNomina: PagoNomina): Long = pagoNominaDao.insert(pagoNomina)

    suspend fun insertAll(pagosNomina: List<PagoNomina>): List<Long> =
        pagoNominaDao.insertAll(pagosNomina)

    suspend fun update(pagoNomina: PagoNomina) = pagoNominaDao.update(pagoNomina)

    suspend fun delete(pagoNomina: PagoNomina) = pagoNominaDao.delete(pagoNomina)
}
