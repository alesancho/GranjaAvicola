package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.ParametrosFiscalesDao
import com.example.granjaavicola.data.entity.ParametrosFiscales
import kotlinx.coroutines.flow.Flow

class ParametrosFiscalesRepository(private val parametrosFiscalesDao: ParametrosFiscalesDao) {

    val parametros: Flow<ParametrosFiscales?> = parametrosFiscalesDao.getParametros()

    suspend fun getParametrosDirecto(): ParametrosFiscales? =
        parametrosFiscalesDao.getParametrosDirecto()

    suspend fun count(): Int = parametrosFiscalesDao.count()

    suspend fun insert(parametros: ParametrosFiscales): Long =
        parametrosFiscalesDao.insert(parametros)

    suspend fun update(parametros: ParametrosFiscales) =
        parametrosFiscalesDao.update(parametros)
}
