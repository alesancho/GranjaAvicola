package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "sanidades",
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
        Index(value = ["fechaProgramada"])
    ]
)
data class Sanidad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loteId: Long,
    val fechaProgramada: LocalDate,
    val fechaAplicada: LocalDate? = null,
    val tipo: TipoSanidad,
    val producto: String,
    val costo: Long = 0L,
    val observaciones: String = ""
)
