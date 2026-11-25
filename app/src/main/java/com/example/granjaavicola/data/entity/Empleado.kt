package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "empleados",
    indices = [
        Index(value = ["documento"])
    ]
)
data class Empleado(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val documento: String,
    val cargo: String,
    val salarioBase: Long,
    val fechaIngreso: LocalDate,
    val activo: Boolean = true
)
