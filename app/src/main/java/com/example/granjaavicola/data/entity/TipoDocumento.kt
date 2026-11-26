package com.example.granjaavicola.data.entity

enum class TipoDocumento(val codigoDian: String, val descripcionDian: String) {
    CC("13", "Cédula de ciudadanía"),
    NIT("31", "NIT"),
    CE("22", "Cédula de extranjería"),
    PASAPORTE("41", "Pasaporte"),
    OTRO("43", "Sin identificación del exterior o para uso definido por la DIAN");

    companion object {
        fun fromCodigoDian(codigo: String): TipoDocumento {
            return entries.firstOrNull { it.codigoDian == codigo.trim() } ?: OTRO
        }
    }
}
