package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.PagoNomina
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoNominaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pagoNomina: PagoNomina): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(pagosNomina: List<PagoNomina>): List<Long>

    @Update
    suspend fun update(pagoNomina: PagoNomina)

    @Delete
    suspend fun delete(pagoNomina: PagoNomina)

    @Query("SELECT * FROM pagos_nomina WHERE id = :id")
    fun getById(id: Long): Flow<PagoNomina?>

    @Query("SELECT * FROM pagos_nomina ORDER BY id DESC")
    fun getAll(): Flow<List<PagoNomina>>

    @Query("SELECT * FROM pagos_nomina WHERE empleadoId = :empleadoId ORDER BY periodo DESC")
    fun getPorEmpleado(empleadoId: Long): Flow<List<PagoNomina>>

    @Query("SELECT * FROM pagos_nomina WHERE periodo = :periodo ORDER BY empleadoId ASC")
    fun getPorPeriodo(periodo: String): Flow<List<PagoNomina>>
}
