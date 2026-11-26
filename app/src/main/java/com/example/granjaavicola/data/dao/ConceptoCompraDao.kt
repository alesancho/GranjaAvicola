package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.ConceptoCompra
import com.example.granjaavicola.data.entity.FaseConcepto
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptoCompraDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(concepto: ConceptoCompra): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conceptos: List<ConceptoCompra>): List<Long>

    @Update
    suspend fun update(concepto: ConceptoCompra)

    @Delete
    suspend fun delete(concepto: ConceptoCompra)

    @Query("SELECT * FROM conceptos_compra WHERE codigo = :codigo")
    fun getByCodigo(codigo: String): Flow<ConceptoCompra?>

    @Query("SELECT * FROM conceptos_compra ORDER BY codigo ASC")
    fun getAll(): Flow<List<ConceptoCompra>>

    @Query("SELECT * FROM conceptos_compra WHERE fase = :fase OR fase = 'AMBAS' ORDER BY codigo ASC")
    fun getPorFase(fase: FaseConcepto): Flow<List<ConceptoCompra>>

    @Query("SELECT COUNT(*) FROM conceptos_compra")
    suspend fun count(): Int
}
