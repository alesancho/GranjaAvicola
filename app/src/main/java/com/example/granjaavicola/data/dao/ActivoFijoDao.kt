package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.ActivoFijo
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivoFijoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(activoFijo: ActivoFijo): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(activosFijos: List<ActivoFijo>): List<Long>

    @Update
    suspend fun update(activoFijo: ActivoFijo)

    @Delete
    suspend fun delete(activoFijo: ActivoFijo)

    @Query("SELECT * FROM activos_fijos WHERE id = :id")
    fun getById(id: Long): Flow<ActivoFijo?>

    @Query("SELECT * FROM activos_fijos ORDER BY fechaCompra DESC")
    fun getAll(): Flow<List<ActivoFijo>>
}
