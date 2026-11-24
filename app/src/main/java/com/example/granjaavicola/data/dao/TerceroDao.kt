package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Tercero
import com.example.granjaavicola.data.entity.TipoTercero
import kotlinx.coroutines.flow.Flow

@Dao
interface TerceroDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(tercero: Tercero): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(terceros: List<Tercero>): List<Long>

    @Update
    suspend fun update(tercero: Tercero)

    @Delete
    suspend fun delete(tercero: Tercero)

    @Query("SELECT * FROM terceros WHERE id = :id")
    fun getById(id: Long): Flow<Tercero?>

    @Query("SELECT * FROM terceros ORDER BY nombre ASC")
    fun getAll(): Flow<List<Tercero>>

    @Query("SELECT * FROM terceros WHERE tipo = :tipo ORDER BY nombre ASC")
    fun getPorTipo(tipo: TipoTercero): Flow<List<Tercero>>

    @Query("SELECT * FROM terceros WHERE tipo = 'CLIENTE' OR tipo = 'AMBOS' ORDER BY nombre ASC")
    fun getClientes(): Flow<List<Tercero>>

    @Query("SELECT * FROM terceros WHERE tipo = 'PROVEEDOR' OR tipo = 'AMBOS' ORDER BY nombre ASC")
    fun getProveedores(): Flow<List<Tercero>>

    @Query("SELECT * FROM terceros WHERE documento = :documento")
    fun getPorDocumento(documento: String): Flow<Tercero?>
}
