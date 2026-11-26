package com.example.granjaavicola.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.granjaavicola.data.entity.TarifaRetencion
import kotlinx.coroutines.flow.Flow

@Dao
interface TarifaRetencionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarifa: TarifaRetencion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarifas: List<TarifaRetencion>): List<Long>

    @Update
    suspend fun update(tarifa: TarifaRetencion)

    @Delete
    suspend fun delete(tarifa: TarifaRetencion)

    @Query("SELECT * FROM tarifas_retencion WHERE concepto = :concepto")
    fun getByConcepto(concepto: String): Flow<TarifaRetencion?>

    @Query("SELECT * FROM tarifas_retencion ORDER BY concepto ASC")
    fun getAll(): Flow<List<TarifaRetencion>>

    @Query("SELECT COUNT(*) FROM tarifas_retencion")
    suspend fun count(): Int
}
