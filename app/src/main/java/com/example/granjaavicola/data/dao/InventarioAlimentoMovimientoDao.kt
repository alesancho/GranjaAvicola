package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.InventarioAlimentoMovimiento
import com.example.granjaavicola.data.entity.TipoMovimientoInventario
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface InventarioAlimentoMovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movimiento: InventarioAlimentoMovimiento): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(movimientos: List<InventarioAlimentoMovimiento>): List<Long>

    @Update
    suspend fun update(movimiento: InventarioAlimentoMovimiento)

    @Delete
    suspend fun delete(movimiento: InventarioAlimentoMovimiento)

    @Query("SELECT * FROM inventario_alimento_movimientos WHERE id = :id")
    fun getById(id: Long): Flow<InventarioAlimentoMovimiento?>

    @Query("SELECT * FROM inventario_alimento_movimientos ORDER BY fecha DESC")
    fun getAll(): Flow<List<InventarioAlimentoMovimiento>>

    @Query("SELECT * FROM inventario_alimento_movimientos ORDER BY fecha ASC")
    fun getAllOrdenadosPorFecha(): Flow<List<InventarioAlimentoMovimiento>>

    @Query("SELECT * FROM inventario_alimento_movimientos WHERE tipo = :tipo ORDER BY fecha DESC")
    fun getPorTipo(tipo: TipoMovimientoInventario): Flow<List<InventarioAlimentoMovimiento>>

    @Query("SELECT * FROM inventario_alimento_movimientos WHERE loteId = :loteId ORDER BY fecha DESC")
    fun getPorLote(loteId: Long): Flow<List<InventarioAlimentoMovimiento>>

    @Query("SELECT * FROM inventario_alimento_movimientos WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha ASC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<InventarioAlimentoMovimiento>>
}
