package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Pago
import com.example.granjaavicola.data.entity.TipoPago
import com.example.granjaavicola.data.entity.TipoReferenciaPago
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PagoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pago: Pago): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(pagos: List<Pago>): List<Long>

    @Update
    suspend fun update(pago: Pago)

    @Delete
    suspend fun delete(pago: Pago)

    @Query("SELECT * FROM pagos WHERE id = :id")
    fun getById(id: Long): Flow<Pago?>

    @Query("SELECT * FROM pagos ORDER BY fecha DESC")
    fun getAll(): Flow<List<Pago>>

    @Query("SELECT * FROM pagos WHERE referenciaId = :referenciaId AND referenciaTipo = :referenciaTipo ORDER BY fecha ASC")
    fun getPorReferencia(referenciaId: Long, referenciaTipo: TipoReferenciaPago): Flow<List<Pago>>

    @Query("SELECT * FROM pagos WHERE tipo = :tipo ORDER BY fecha DESC")
    fun getPorTipo(tipo: TipoPago): Flow<List<Pago>>

    @Query("SELECT * FROM pagos WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Pago>>
}
