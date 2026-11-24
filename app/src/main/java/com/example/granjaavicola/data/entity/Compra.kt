package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "compras",
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
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: LocalDate,
    val terceroId: Long? = null,
    val categoria: CategoriaCompra,
    val descripcion: String,
    val cantidad: Double,
    val unidad: String,
    val valorUnitario: Long,
    val iva: Long = 0L,
    val retencion: Long = 0L,
    val total: Long,
    val loteId: Long? = null,
    val pagada: Boolean = false
)
