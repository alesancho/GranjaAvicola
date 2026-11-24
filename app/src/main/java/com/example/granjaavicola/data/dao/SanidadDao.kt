package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Sanidad
import com.example.granjaavicola.data.entity.TipoSanidad
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SanidadDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(sanidad: Sanidad): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(sanidades: List<Sanidad>): List<Long>

    @Update
    suspend fun update(sanidad: Sanidad)

    @Delete
    suspend fun delete(sanidad: Sanidad)

    @Query("SELECT * FROM sanidades WHERE id = :id")
    fun getById(id: Long): Flow<Sanidad?>

    @Query("SELECT * FROM sanidades ORDER BY fechaProgramada DESC")
    fun getAll(): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE loteId = :loteId ORDER BY fechaProgramada ASC")
    fun getPorLote(loteId: Long): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE fechaAplicada IS NULL ORDER BY fechaProgramada ASC")
    fun getPendientes(): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE loteId = :loteId AND fechaAplicada IS NULL ORDER BY fechaProgramada ASC")
    fun getPendientesPorLote(loteId: Long): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE loteId = :loteId AND fechaAplicada IS NOT NULL ORDER BY fechaAplicada DESC")
    fun getAplicadasPorLote(loteId: Long): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE tipo = :tipo ORDER BY fechaProgramada DESC")
    fun getPorTipo(tipo: TipoSanidad): Flow<List<Sanidad>>

    @Query("SELECT * FROM sanidades WHERE fechaProgramada BETWEEN :fechaInicio AND :fechaFin ORDER BY fechaProgramada ASC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Sanidad>>
}
