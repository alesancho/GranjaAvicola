package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "pagos",
    indices = [
        Index(value = ["referenciaId", "referenciaTipo"]),
        Index(value = ["fecha"])
    ]
)
data class Pago(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: TipoPago,
    val referenciaId: Long,
    val referenciaTipo: TipoReferenciaPago,
    val fecha: LocalDate,
    val valor: Long,
    val medio: String
)
