package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parametros_fiscales")
data class ParametrosFiscales(
    @PrimaryKey
    val id: Int = 1,
    val razonSocial: String = "",
    val nit: String = "",
    val dv: String = "",
    val anioGravable: Int = 2026,
    val tipoPersona: TipoPersona = TipoPersona.JURIDICA,
    val regimen: RegimenTributario = RegimenTributario.ORDINARIO,
    val responsableIva: Boolean = false,
    val agenteRetenedor: Boolean = false,
    val municipio: String = "",
    val ciiu: String = "",
    val uvt: Long = 52_374L,
    val tarifaGmf: Double = 0.004,
    val tasaSaludEmpleado: Double = 0.04,
    val tasaPensionEmpleado: Double = 0.04,
    val tasaPensionEmpleador: Double = 0.12,
    val tasaCaja: Double = 0.04,
    val tasaCesantias: Double = 0.0833,
    val tasaInteresesCesantias: Double = 0.01,
    val tasaPrima: Double = 0.0833,
    val tasaVacaciones: Double = 0.0417,
    val reteIcaCompras: Double = 0.0
)
