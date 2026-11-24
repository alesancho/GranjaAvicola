package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.RegistroDiario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface RegistroDiarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(registro: RegistroDiario): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(registros: List<RegistroDiario>): List<Long>

    @Update
    suspend fun update(registro: RegistroDiario)

    @Delete
    suspend fun delete(registro: RegistroDiario)

    @Query("SELECT * FROM registros_diarios WHERE id = :id")
    fun getById(id: Long): Flow<RegistroDiario?>

    @Query("SELECT * FROM registros_diarios WHERE loteId = :loteId ORDER BY fecha ASC")
    fun getRegistrosPorLote(loteId: Long): Flow<List<RegistroDiario>>

    @Query("SELECT * FROM registros_diarios WHERE loteId = :loteId AND fecha = :fecha")
    fun getRegistroPorLoteYFecha(loteId: Long, fecha: LocalDate): Flow<RegistroDiario?>

    @Query("SELECT * FROM registros_diarios WHERE loteId = :loteId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha ASC")
    fun getRegistrosPorLoteYRangoFecha(
        loteId: Long,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<RegistroDiario>>

    @Query("SELECT * FROM registros_diarios WHERE loteId = :loteId ORDER BY fecha DESC LIMIT 1")
    fun getUltimoRegistroPorLote(loteId: Long): Flow<RegistroDiario?>
}
