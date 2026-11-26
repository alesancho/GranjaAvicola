package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "movimientos_bancarios",
    foreignKeys = [
        ForeignKey(
            entity = Tercero::class,
            parentColumns = ["id"],
            childColumns = ["terceroId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["terceroId"]),
        Index(value = ["fecha"]),
        Index(value = ["cuenta"])
    ]
)
data class MovimientoBancario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: LocalDate,
    val descripcion: String,
    val valor: Long,
    val tipo: TipoMovimientoBancario,
    val banco: String = "",
    val cuenta: String,
    val medio: MedioMovimientoBancario = MedioMovimientoBancario.TRANSFERENCIA,
    val terceroId: Long? = null,
    val sujetoGmf: Boolean = false,
    val gmfEstimado: Long = 0L,
    val soporte: String? = null,
    val conciliado: Boolean = false
)
