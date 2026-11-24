package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "pesajes_ave",
    foreignKeys = [
        ForeignKey(
            entity = Lote::class,
            parentColumns = ["id"],
            childColumns = ["loteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["loteId"])
    ]
)
data class PesajeAve(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loteId: Long,
    val fecha: LocalDate,
    val semana: Int,
    val pesoG: Double
)
