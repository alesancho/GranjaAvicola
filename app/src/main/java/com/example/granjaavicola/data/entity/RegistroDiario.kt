package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "registros_diarios",
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
data class RegistroDiario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loteId: Long,
    val fecha: LocalDate,
    val consumoKg: Double,
    val aguaLitros: Double,
    val mortalidad: Int,
    val descarte: Int,
    val temperaturaMax: Double,
    val temperaturaMin: Double,
    val horasLuz: Double,
    val observaciones: String = ""
)
