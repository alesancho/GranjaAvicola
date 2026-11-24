package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "galpones")
data class Galpon(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val capacidad: Int
)
