package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.ParametrosFiscales
import kotlinx.coroutines.flow.Flow

@Dao
interface ParametrosFiscalesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parametros: ParametrosFiscales): Long

    @Update
    suspend fun update(parametros: ParametrosFiscales)

    @Query("SELECT * FROM parametros_fiscales WHERE id = 1 LIMIT 1")
    fun getParametros(): Flow<ParametrosFiscales?>

    @Query("SELECT * FROM parametros_fiscales WHERE id = 1 LIMIT 1")
    suspend fun getParametrosDirecto(): ParametrosFiscales?

    @Query("SELECT COUNT(*) FROM parametros_fiscales")
    suspend fun count(): Int
}
