package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "producciones_huevo",
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
        Index(value = ["loteId", "fecha"], unique = true)
    ]
)
data class ProduccionHuevo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loteId: Long,
    val fecha: LocalDate,
    val huevosAA: Int,
    val huevosA: Int,
    val huevosB: Int,
    val huevosC: Int,
    val huevosRotos: Int,
    val huevosSucios: Int
)
