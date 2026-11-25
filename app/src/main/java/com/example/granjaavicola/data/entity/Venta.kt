package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Tercero::class,
            parentColumns = ["id"],
            childColumns = ["terceroId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Lote::class,
            parentColumns = ["id"],
            childColumns = ["loteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["terceroId"]),
        Index(value = ["loteId"]),
        Index(value = ["fecha"])
    ]
)
data class Venta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: LocalDate,
    val terceroId: Long? = null,
    val producto: ProductoVenta,
    val cantidad: Double,
    val unidad: String,
    val valorUnitario: Long,
    val subtotal: Long = 0L,
    val iva: Long = 0L,
    val retencion: Long = 0L,
    val reteFuente: Long = 0L,
    val reteIva: Long = 0L,
    val reteIca: Long = 0L,
    val total: Long,
    val netoAPagar: Long = 0L,
    val numeroFactura: String? = null,
    val cuifeCufe: String? = null,
    val conceptoRetencion: String? = null,
    val loteId: Long? = null
)
