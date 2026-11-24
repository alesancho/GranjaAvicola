package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.PesajeAve
import kotlinx.coroutines.flow.Flow

@Dao
interface PesajeAveDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pesaje: PesajeAve): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(pesajes: List<PesajeAve>): List<Long>

    @Update
    suspend fun update(pesaje: PesajeAve)

    @Delete
    suspend fun delete(pesaje: PesajeAve)

    @Query("SELECT * FROM pesajes_ave WHERE id = :id")
    fun getById(id: Long): Flow<PesajeAve?>

    @Query("SELECT * FROM pesajes_ave WHERE loteId = :loteId ORDER BY fecha ASC, semana ASC")
    fun getPesajesPorLote(loteId: Long): Flow<List<PesajeAve>>

    @Query("SELECT * FROM pesajes_ave WHERE loteId = :loteId AND semana = :semana ORDER BY fecha ASC")
    fun getPesajesPorLoteYSemana(loteId: Long, semana: Int): Flow<List<PesajeAve>>
}
