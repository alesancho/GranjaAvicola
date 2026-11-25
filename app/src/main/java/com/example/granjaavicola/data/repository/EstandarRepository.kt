package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.EstandarDao
import com.example.granjaavicola.data.entity.Estandar
import com.example.granjaavicola.data.entity.FaseEstandar
import kotlinx.coroutines.flow.Flow

class EstandarRepository(private val estandarDao: EstandarDao) {

    val allEstandares: Flow<List<Estandar>> = estandarDao.getAll()

    fun getById(id: Long): Flow<Estandar?> = estandarDao.getById(id)

    fun getPorLinea(linea: String): Flow<List<Estandar>> =
        estandarDao.getEstandaresPorLinea(linea)

    fun getPorLineaYFase(linea: String, fase: FaseEstandar): Flow<List<Estandar>> =
        estandarDao.getEstandaresPorLineaYFase(linea, fase)

    fun getPorLineaYSemana(linea: String, semanaEdad: Int): Flow<Estandar?> =
        estandarDao.getEstandarPorLineaYSemana(linea, semanaEdad)

    fun getPorLineaSemanaYFase(
        linea: String,
        semanaEdad: Int,
        fase: FaseEstandar
    ): Flow<Estandar?> = estandarDao.getEstandarPorLineaSemanaYFase(linea, semanaEdad, fase)

    suspend fun count(): Int = estandarDao.count()

    suspend fun insert(estandar: Estandar): Long = estandarDao.insert(estandar)

    suspend fun insertAll(estandares: List<Estandar>): List<Long> =
        estandarDao.insertAll(estandares)

    suspend fun update(estandar: Estandar) = estandarDao.update(estandar)

    suspend fun delete(estandar: Estandar) = estandarDao.delete(estandar)
}
