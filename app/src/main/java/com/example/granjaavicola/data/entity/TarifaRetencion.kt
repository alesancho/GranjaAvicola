package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarifas_retencion")
data class TarifaRetencion(
    @PrimaryKey
    val concepto: String,
    val tarifa: Double,
    val baseMinimaUvt: Int
)
