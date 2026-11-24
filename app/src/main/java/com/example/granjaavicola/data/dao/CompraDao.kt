package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.CategoriaCompra
import com.example.granjaavicola.data.entity.Compra
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CompraDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(compra: Compra): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(compras: List<Compra>): List<Long>

    @Update
    suspend fun update(compra: Compra)

    @Delete
    suspend fun delete(compra: Compra)

    @Query("SELECT * FROM compras WHERE id = :id")
    fun getById(id: Long): Flow<Compra?>

    @Query("SELECT * FROM compras ORDER BY fecha DESC")
    fun getAll(): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getPorRangoFecha(fechaInicio: LocalDate, fechaFin: LocalDate): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE terceroId = :terceroId ORDER BY fecha DESC")
    fun getPorTercero(terceroId: Long): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE loteId = :loteId ORDER BY fecha DESC")
    fun getPorLote(loteId: Long): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE categoria = :categoria ORDER BY fecha DESC")
    fun getPorCategoria(categoria: CategoriaCompra): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE pagada = :pagada ORDER BY fecha DESC")
    fun getPorEstadoPago(pagada: Boolean): Flow<List<Compra>>
}
