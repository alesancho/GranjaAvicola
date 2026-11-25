package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.Empleado
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(empleado: Empleado): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(empleados: List<Empleado>): List<Long>

    @Update
    suspend fun update(empleado: Empleado)

    @Delete
    suspend fun delete(empleado: Empleado)

    @Query("SELECT * FROM empleados WHERE id = :id")
    fun getById(id: Long): Flow<Empleado?>

    @Query("SELECT * FROM empleados ORDER BY nombre ASC")
    fun getAll(): Flow<List<Empleado>>

    @Query("SELECT * FROM empleados WHERE activo = :activo ORDER BY nombre ASC")
    fun getActivos(activo: Boolean = true): Flow<List<Empleado>>

    @Query("SELECT * FROM empleados WHERE documento = :documento")
    fun getPorDocumento(documento: String): Flow<Empleado?>
}
