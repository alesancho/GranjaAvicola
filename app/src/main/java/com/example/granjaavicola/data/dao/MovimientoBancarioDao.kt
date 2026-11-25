package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.MovimientoBancario
import com.example.granjaavicola.data.entity.TipoMovimientoBancario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MovimientoBancarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movimiento: MovimientoBancario): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(movimientos: List<MovimientoBancario>): List<Long>

    @Update
    suspend fun update(movimiento: MovimientoBancario)

    @Delete
    suspend fun delete(movimiento: MovimientoBancario)

    @Query("SELECT * FROM movimientos_bancarios WHERE id = :id")
    fun getById(id: Long): Flow<MovimientoBancario?>

    @Query("SELECT * FROM movimientos_bancarios ORDER BY fecha DESC")
    fun getAll(): Flow<List<MovimientoBancario>>

    @Query("SELECT * FROM movimientos_bancarios WHERE cuenta = :cuenta ORDER BY fecha DESC")
    fun getPorCuenta(cuenta: String): Flow<List<MovimientoBancario>>

    @Query("SELECT * FROM movimientos_bancarios WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<MovimientoBancario>>

    @Query("SELECT * FROM movimientos_bancarios WHERE tipo = :tipo ORDER BY fecha DESC")
    fun getPorTipo(tipo: TipoMovimientoBancario): Flow<List<MovimientoBancario>>

    @Query("SELECT * FROM movimientos_bancarios WHERE conciliado = :conciliado ORDER BY fecha DESC")
    fun getPorConciliado(conciliado: Boolean): Flow<List<MovimientoBancario>>

    @Query("SELECT * FROM movimientos_bancarios WHERE terceroId = :terceroId ORDER BY fecha DESC")
    fun getPorTercero(terceroId: Long): Flow<List<MovimientoBancario>>
}
