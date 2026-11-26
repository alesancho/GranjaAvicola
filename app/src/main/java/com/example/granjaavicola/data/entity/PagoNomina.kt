package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registro de liquidación y pago de nómina de un empleado en un período.
 *
 * Relación de liquidación (COP):
 * `netoPagado = salario + auxilioTransporte + horasExtra - saludEmpleado - pensionEmpleado`
 */
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
    val tipoContrato: String = "",
    val salario: Long,
    val auxilioTransporte: Long,
    val horasExtra: Long = 0L,
    val saludEmpleado: Long = 0L,
    val pensionEmpleado: Long = 0L,
    val deducciones: Long = 0L,
    val netoPagado: Long,
    val pensionEmpleador: Long = 0L,
    val cajaCompensacion: Long = 0L,
    val saludArl: Long = 0L,
    val provisionPrestaciones: Long = 0L,
    val costoTotalEmpleador: Long = 0L
)
