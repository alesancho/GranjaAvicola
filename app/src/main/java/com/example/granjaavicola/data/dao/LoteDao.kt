package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.Lote
import kotlinx.coroutines.flow.Flow

@Dao
interface LoteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(lote: Lote): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(lotes: List<Lote>): List<Long>

    @Update
    suspend fun update(lote: Lote)

    @Delete
    suspend fun delete(lote: Lote)

    @Query("SELECT * FROM lotes WHERE id = :id")
    fun getById(id: Long): Flow<Lote?>

    @Query("SELECT * FROM lotes ORDER BY fechaLlegada DESC")
    fun getAll(): Flow<List<Lote>>

    @Query("SELECT * FROM lotes WHERE estado != 'CERRADO' ORDER BY fechaLlegada DESC")
    fun getLotesActivos(): Flow<List<Lote>>

    @Query("SELECT * FROM lotes WHERE estado = :estado ORDER BY fechaLlegada DESC")
    fun getLotesPorEstado(estado: EstadoLote): Flow<List<Lote>>

    @Query("SELECT * FROM lotes WHERE galponId = :galponId ORDER BY fechaLlegada DESC")
    fun getLotesPorGalpon(galponId: Long): Flow<List<Lote>>
}
