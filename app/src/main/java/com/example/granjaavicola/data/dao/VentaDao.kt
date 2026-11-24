package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.ProductoVenta
import com.example.granjaavicola.data.entity.Venta
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(venta: Venta): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(ventas: List<Venta>): List<Long>

    @Update
    suspend fun update(venta: Venta)

    @Delete
    suspend fun delete(venta: Venta)

    @Query("SELECT * FROM ventas WHERE id = :id")
    fun getById(id: Long): Flow<Venta?>

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getAll(): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE terceroId = :terceroId ORDER BY fecha DESC")
    fun getPorTercero(terceroId: Long): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE loteId = :loteId ORDER BY fecha DESC")
    fun getPorLote(loteId: Long): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE producto = :producto ORDER BY fecha DESC")
    fun getPorProducto(producto: ProductoVenta): Flow<List<Venta>>
}
