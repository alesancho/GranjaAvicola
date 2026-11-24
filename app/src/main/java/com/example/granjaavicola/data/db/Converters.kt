package com.example.granjaavicola.data.db

import androidx.room.TypeConverter
import com.example.granjaavicola.data.entity.CategoriaCompra
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.FaseEstandar
import com.example.granjaavicola.data.entity.ProductoVenta
import com.example.granjaavicola.data.entity.TipoMovimientoInventario
import com.example.granjaavicola.data.entity.TipoPago
import com.example.granjaavicola.data.entity.TipoReferenciaPago
import com.example.granjaavicola.data.entity.TipoSanidad
import com.example.granjaavicola.data.entity.TipoTercero
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromEstadoLote(value: String?): EstadoLote? {
        return value?.let { enumValueOf<EstadoLote>(it) }
    }

    @TypeConverter
    fun toEstadoLote(estado: EstadoLote?): String? {
        return estado?.name
    }

    @TypeConverter
    fun fromFaseEstandar(value: String?): FaseEstandar? {
        return value?.let { enumValueOf<FaseEstandar>(it) }
    }

    @TypeConverter
    fun toFaseEstandar(fase: FaseEstandar?): String? {
        return fase?.name
    }

    @TypeConverter
    fun fromTipoTercero(value: String?): TipoTercero? {
        return value?.let { enumValueOf<TipoTercero>(it) }
    }

    @TypeConverter
    fun toTipoTercero(tipo: TipoTercero?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromCategoriaCompra(value: String?): CategoriaCompra? {
        return value?.let { enumValueOf<CategoriaCompra>(it) }
    }

    @TypeConverter
    fun toCategoriaCompra(categoria: CategoriaCompra?): String? {
        return categoria?.name
    }

    @TypeConverter
    fun fromProductoVenta(value: String?): ProductoVenta? {
        return value?.let { enumValueOf<ProductoVenta>(it) }
    }

    @TypeConverter
    fun toProductoVenta(producto: ProductoVenta?): String? {
        return producto?.name
    }

    @TypeConverter
    fun fromTipoPago(value: String?): TipoPago? {
        return value?.let { enumValueOf<TipoPago>(it) }
    }

    @TypeConverter
    fun toTipoPago(tipo: TipoPago?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromTipoReferenciaPago(value: String?): TipoReferenciaPago? {
        return value?.let { enumValueOf<TipoReferenciaPago>(it) }
    }

    @TypeConverter
    fun toTipoReferenciaPago(referenciaTipo: TipoReferenciaPago?): String? {
        return referenciaTipo?.name
    }

    @TypeConverter
    fun fromTipoMovimientoInventario(value: String?): TipoMovimientoInventario? {
        return value?.let { enumValueOf<TipoMovimientoInventario>(it) }
    }

    @TypeConverter
    fun toTipoMovimientoInventario(tipo: TipoMovimientoInventario?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromTipoSanidad(value: String?): TipoSanidad? {
        return value?.let { enumValueOf<TipoSanidad>(it) }
    }

    @TypeConverter
    fun toTipoSanidad(tipo: TipoSanidad?): String? {
        return tipo?.name
    }
}
