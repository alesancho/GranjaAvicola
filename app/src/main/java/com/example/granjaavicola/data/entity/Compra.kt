package com.example.granjaavicola.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Representa una compra o gasto registrado en la granja.
 *
 * Relación contable de montos (todos los valores monetarios en Long COP):
 * - `subtotal` = base gravable
 * - `iva` = `subtotal * tarifaIva`
 * - `total` = `subtotal + iva`
 * - `netoAPagar` = `total - reteFuente - reteIva - reteIca`
 */
@Entity(
    tableName = "compras",
    foreignKeys = [
        ForeignKey(
            entity = Tercero::class,
            parentColumns = ["id"],
            childColumns = ["terceroId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Lote::class,
            parentColumns = ["id"],
            childColumns = ["loteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["terceroId"]),
        Index(value = ["loteId"]),
        Index(value = ["fecha"]),
        Index(value = ["codigoConcepto"])
    ]
)
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: LocalDate,
    val terceroId: Long? = null,
    val tipoSoporte: TipoSoporte = TipoSoporte.FACTURA_ELECTRONICA,
    val codigoConcepto: String = "OTR",
    val categoria: CategoriaCompra,
    val descripcion: String,
    val cantidad: Double,
    val unidad: String,
    val valorUnitario: Long,
    /** Base gravable de la compra (COP). */
    val subtotal: Long = 0L,
    /** Tarifa de IVA aplicable: 0.0, 0.05 o 0.19. */
    val tarifaIva: Double = 0.0,
    /** Valor del IVA: `subtotal * tarifaIva` (COP). */
    val iva: Long = 0L,
    val reteFuente: Long = 0L,
    val reteIva: Long = 0L,
    val reteIca: Long = 0L,
    /** Total facturado: `subtotal + iva` (COP). */
    val total: Long,
    /** Valor neto a desembolsar: `total - reteFuente - reteIva - reteIca` (COP). */
    val netoAPagar: Long = 0L,
    val numeroFactura: String? = null,
    val cuifeCufe: String? = null,
    val conceptoRetencion: String? = null,
    val loteId: Long? = null,
    val pagada: Boolean = false
)
