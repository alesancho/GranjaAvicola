package com.example.granjaavicola.data.repository

import com.example.granjaavicola.data.dao.ConceptoCompraDao
import com.example.granjaavicola.data.entity.ConceptoCompra
import com.example.granjaavicola.data.entity.FaseConcepto
import kotlinx.coroutines.flow.Flow

class ConceptoCompraRepository(private val conceptoCompraDao: ConceptoCompraDao) {

    val allConceptos: Flow<List<ConceptoCompra>> = conceptoCompraDao.getAll()

    fun getByCodigo(codigo: String): Flow<ConceptoCompra?> =
        conceptoCompraDao.getByCodigo(codigo)

    fun getPorFase(fase: FaseConcepto): Flow<List<ConceptoCompra>> =
        conceptoCompraDao.getPorFase(fase)

    suspend fun count(): Int = conceptoCompraDao.count()

    suspend fun insert(concepto: ConceptoCompra): Long = conceptoCompraDao.insert(concepto)

    suspend fun insertAll(conceptos: List<ConceptoCompra>): List<Long> =
        conceptoCompraDao.insertAll(conceptos)

    suspend fun update(concepto: ConceptoCompra) = conceptoCompraDao.update(concepto)

    suspend fun delete(concepto: ConceptoCompra) = conceptoCompraDao.delete(concepto)
}
