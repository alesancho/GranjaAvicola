package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "lotes",
    foreignKeys = [
        ForeignKey(
            entity = Galpon::class,
            parentColumns = ["id"],
            childColumns = ["galponId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["galponId"])
    ]
)
data class Lote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val numeroLote: String,
    val galponId: Long,
    val fechaNacimiento: LocalDate,
    val fechaLlegada: LocalDate,
    val avesIniciales: Int,
    val pesoInicialG: Double,
    val lineaGenetica: String,
    val proveedor: String,
    val costoPorPollita: Long,
    val estado: EstadoLote,
    val fechaTraslado: LocalDate? = null,
    val fechaCierre: LocalDate? = null
)
