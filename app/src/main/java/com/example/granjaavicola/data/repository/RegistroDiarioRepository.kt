package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.RegistroDiarioDao
import com.example.granjaavicola.data.entity.RegistroDiario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RegistroDiarioRepository(private val registroDiarioDao: RegistroDiarioDao) {

    fun getById(id: Long): Flow<RegistroDiario?> = registroDiarioDao.getById(id)

    fun getPorLote(loteId: Long): Flow<List<RegistroDiario>> =
        registroDiarioDao.getRegistrosPorLote(loteId)

    fun getPorLoteYFecha(loteId: Long, fecha: LocalDate): Flow<RegistroDiario?> =
        registroDiarioDao.getRegistroPorLoteYFecha(loteId, fecha)

    fun getPorLoteYRangoFecha(
        loteId: Long,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<RegistroDiario>> =
        registroDiarioDao.getRegistrosPorLoteYRangoFecha(loteId, fechaInicio, fechaFin)

    fun getUltimoRegistroPorLote(loteId: Long): Flow<RegistroDiario?> =
        registroDiarioDao.getUltimoRegistroPorLote(loteId)

    suspend fun insert(registro: RegistroDiario): Long = registroDiarioDao.insert(registro)

    suspend fun insertAll(registros: List<RegistroDiario>): List<Long> =
        registroDiarioDao.insertAll(registros)

    suspend fun update(registro: RegistroDiario) = registroDiarioDao.update(registro)

    suspend fun delete(registro: RegistroDiario) = registroDiarioDao.delete(registro)
}
