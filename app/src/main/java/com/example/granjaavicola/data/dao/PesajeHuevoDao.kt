package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.PesajeHuevo
import kotlinx.coroutines.flow.Flow

@Dao
interface PesajeHuevoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pesaje: PesajeHuevo): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(pesajes: List<PesajeHuevo>): List<Long>

    @Update
    suspend fun update(pesaje: PesajeHuevo)

    @Delete
    suspend fun delete(pesaje: PesajeHuevo)

    @Query("SELECT * FROM pesajes_huevo WHERE id = :id")
    fun getById(id: Long): Flow<PesajeHuevo?>

    @Query("SELECT * FROM pesajes_huevo WHERE loteId = :loteId ORDER BY fecha ASC, semana ASC")
    fun getPesajesPorLote(loteId: Long): Flow<List<PesajeHuevo>>

    @Query("SELECT * FROM pesajes_huevo WHERE loteId = :loteId AND semana = :semana ORDER BY fecha ASC")
    fun getPesajesPorLoteYSemana(loteId: Long, semana: Int): Flow<List<PesajeHuevo>>
}
