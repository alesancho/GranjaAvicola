package com.example.granjaavicola.domain.modelos

/**
 * Tipo de movimiento de inventario de alimento.
 */
enum class TipoMovimiento {
    ENTRADA,
    SALIDA
}

/**
 * Representa un movimiento individual de alimento en el inventario.
 *
 * @param tipo Tipo de movimiento: [TipoMovimiento.ENTRADA] o [TipoMovimiento.SALIDA].
 * @param kg Cantidad de alimento en kilogramos.
 * @param valorTotal Valor monetario total del movimiento (en pesos). Para salidas puede omitirse (0L).
 */
data class MovimientoAlimento(
    val tipo: TipoMovimiento,
    val kg: Double,
    val valorTotal: Long = 0L
)

/**
 * Estado resultante del inventario de alimento con costo promedio ponderado.
 *
 * @param stockKg Kilogramos disponibles en inventario.
 * @param valorStock Valor monetario total del inventario en pesos.
 * @param costoPromedioPorKg Costo promedio ponderado vigente por kilogramo en pesos.
 */
data class EstadoInventario(
    val stockKg: Double,
    val valorStock: Double,
    val costoPromedioPorKg: Double
)
