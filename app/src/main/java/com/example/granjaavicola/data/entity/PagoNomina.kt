package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagos_nomina",
    foreignKeys = [
        ForeignKey(
            entity = Empleado::class,
            parentColumns = ["id"],
            childColumns = ["empleadoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["empleadoId"]),
        Index(value = ["periodo"])
    ]
)
data class PagoNomina(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val empleadoId: Long,
    val periodo: String,
    val salario: Long,
    val auxilioTransporte: Long,
    val deducciones: Long,
    val netoPagado: Long
)
