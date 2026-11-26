package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "activos_fijos",
    foreignKeys = [
        ForeignKey(
            entity = Tercero::class,
            parentColumns = ["id"],
            childColumns = ["terceroId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["fechaCompra"]),
        Index(value = ["terceroId"]),
        Index(value = ["codigo"])
    ]
)
data class ActivoFijo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigo: String = "",
    val descripcion: String,
    val fechaCompra: LocalDate,
    val terceroId: Long? = null,
    val factura: String? = null,
    val valorCompra: Long,
    val vidaUtilMeses: Int,
    val depreciacionAcumulada: Long = 0L
)
