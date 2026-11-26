package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conceptos_compra")
data class ConceptoCompra(
    @PrimaryKey
    val codigo: String,
    val nombre: String,
    val fase: FaseConcepto,
    val naturaleza: String,
    val retencionSugerida: String
)
