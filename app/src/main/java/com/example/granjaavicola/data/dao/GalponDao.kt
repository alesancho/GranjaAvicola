package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Galpon
import kotlinx.coroutines.flow.Flow

@Dao
interface GalponDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(galpon: Galpon): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(galpones: List<Galpon>): List<Long>

    @Update
    suspend fun update(galpon: Galpon)

    @Delete
    suspend fun delete(galpon: Galpon)

    @Query("SELECT * FROM galpones WHERE id = :id")
    fun getById(id: Long): Flow<Galpon?>

    @Query("SELECT * FROM galpones ORDER BY nombre ASC")
    fun getAll(): Flow<List<Galpon>>
}
