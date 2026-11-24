package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Estandar
import com.example.granjaavicola.data.entity.FaseEstandar
import kotlinx.coroutines.flow.Flow

@Dao
interface EstandarDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(estandar: Estandar): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(estandares: List<Estandar>): List<Long>

    @Update
    suspend fun update(estandar: Estandar)

    @Delete
    suspend fun delete(estandar: Estandar)

    @Query("SELECT * FROM estandares WHERE id = :id")
    fun getById(id: Long): Flow<Estandar?>

    @Query("SELECT * FROM estandares ORDER BY linea ASC, semanaEdad ASC")
    fun getAll(): Flow<List<Estandar>>

    @Query("SELECT * FROM estandares WHERE linea = :linea ORDER BY semanaEdad ASC")
    fun getEstandaresPorLinea(linea: String): Flow<List<Estandar>>

    @Query("SELECT * FROM estandares WHERE linea = :linea AND fase = :fase ORDER BY semanaEdad ASC")
    fun getEstandaresPorLineaYFase(linea: String, fase: FaseEstandar): Flow<List<Estandar>>

    @Query("SELECT * FROM estandares WHERE linea = :linea AND semanaEdad = :semanaEdad LIMIT 1")
    fun getEstandarPorLineaYSemana(linea: String, semanaEdad: Int): Flow<Estandar?>
}
