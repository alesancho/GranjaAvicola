package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.ProduccionHuevo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProduccionHuevoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(produccion: ProduccionHuevo): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(producciones: List<ProduccionHuevo>): List<Long>

    @Update
    suspend fun update(produccion: ProduccionHuevo)

    @Delete
    suspend fun delete(produccion: ProduccionHuevo)

    @Query("SELECT * FROM producciones_huevo WHERE id = :id")
    fun getById(id: Long): Flow<ProduccionHuevo?>

    @Query("SELECT * FROM producciones_huevo WHERE loteId = :loteId ORDER BY fecha ASC")
    fun getProduccionPorLote(loteId: Long): Flow<List<ProduccionHuevo>>

    @Query("SELECT * FROM producciones_huevo WHERE loteId = :loteId AND fecha = :fecha")
    fun getProduccionPorLoteYFecha(loteId: Long, fecha: LocalDate): Flow<ProduccionHuevo?>

    @Query("SELECT * FROM producciones_huevo WHERE loteId = :loteId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha ASC")
    fun getProduccionPorLoteYRangoFecha(
        loteId: Long,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<ProduccionHuevo>>
}
