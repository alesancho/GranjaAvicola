package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.SanidadDao
import com.example.granjaavicola.data.entity.Sanidad
import com.example.granjaavicola.data.entity.TipoSanidad
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class SanidadRepository(private val sanidadDao: SanidadDao) {

    val allSanidades: Flow<List<Sanidad>> = sanidadDao.getAll()

    val pendientes: Flow<List<Sanidad>> = sanidadDao.getPendientes()

    fun getById(id: Long): Flow<Sanidad?> = sanidadDao.getById(id)

    fun getPorLote(loteId: Long): Flow<List<Sanidad>> = sanidadDao.getPorLote(loteId)

    fun getPendientesPorLote(loteId: Long): Flow<List<Sanidad>> =
        sanidadDao.getPendientesPorLote(loteId)

    fun getAplicadasPorLote(loteId: Long): Flow<List<Sanidad>> =
        sanidadDao.getAplicadasPorLote(loteId)

    fun getPorTipo(tipo: TipoSanidad): Flow<List<Sanidad>> = sanidadDao.getPorTipo(tipo)

    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Sanidad>> =
        sanidadDao.getPorRangoFecha(fechaInicio, fechaFin)

    suspend fun insert(sanidad: Sanidad): Long = sanidadDao.insert(sanidad)

    suspend fun insertAll(sanidades: List<Sanidad>): List<Long> = sanidadDao.insertAll(sanidades)

    suspend fun update(sanidad: Sanidad) = sanidadDao.update(sanidad)

    suspend fun delete(sanidad: Sanidad) = sanidadDao.delete(sanidad)
}
