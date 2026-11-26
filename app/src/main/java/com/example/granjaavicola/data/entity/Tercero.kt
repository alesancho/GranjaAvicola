package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "terceros",
    indices = [
        Index(value = ["documento"])
    ]
)
data class Tercero(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val primerApellido: String? = null,
    val segundoApellido: String? = null,
    val primerNombre: String? = null,
    val otrosNombres: String? = null,
    val tipoDocumento: TipoDocumento = TipoDocumento.CC,
    val documento: String,
    val digitoVerificacion: Int? = null,
    val tipo: TipoTercero,
    val telefono: String? = null,
    val direccion: String? = null,
    val correo: String? = null,
    val ciudad: String? = null,
    val codDepartamento: String? = null,
    val codMunicipio: String? = null,
    val codPais: String = "169",
    val responsableIva: Boolean = false,
    val obligadoFacturar: Boolean = false
)
