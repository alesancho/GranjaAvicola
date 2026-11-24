package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "inventario_alimento_movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Lote::class,
            parentColumns = ["id"],
            childColumns = ["loteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["loteId"]),
        Index(value = ["fecha"])
    ]
)
data class InventarioAlimentoMovimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: LocalDate,
    val tipo: TipoMovimientoInventario,
    val kg: Double,
    val valorTotal: Long,
    val loteId: Long? = null
)
