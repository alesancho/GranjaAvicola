package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estandares",
    indices = [
        Index(value = ["linea", "semanaEdad"], unique = true)
    ]
)
data class Estandar(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val linea: String,
    val semanaEdad: Int,
    val fase: FaseEstandar,
    val pesoAveG: Double? = null,
    val consumoGAveDia: Double? = null,
    val posturaPct: Double? = null,
    val pesoHuevoG: Double? = null
)
