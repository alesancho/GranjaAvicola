package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "activos_fijos",
    indices = [
        Index(value = ["fechaCompra"])
    ]
)
data class ActivoFijo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descripcion: String,
    val fechaCompra: LocalDate,
    val valorCompra: Long,
    val vidaUtilMeses: Int,
    val depreciacionAcumulada: Long = 0L
)
