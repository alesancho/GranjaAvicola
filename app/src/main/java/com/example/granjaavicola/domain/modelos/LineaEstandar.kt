package com.example.granjaavicola.domain.modelos

enum class LineaEstandar(
    val codigo: String,
    val etiqueta: String
) {
    ISA_BROWN_PISO("ISA_BROWN_PISO", "ISA Brown - piso"),
    ISA_BROWN_JAULA("ISA_BROWN_JAULA", "ISA Brown - jaula");

    companion object {
        val POR_DEFECTO: LineaEstandar = ISA_BROWN_PISO

        fun desdeCodigo(codigo: String?): LineaEstandar =
            entries.firstOrNull { it.codigo == codigo } ?: POR_DEFECTO
    }
}
