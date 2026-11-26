package com.example.granjaavicola.data.db

import androidx.room.TypeConverter
import com.example.granjaavicola.data.entity.CategoriaCompra
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.FaseConcepto
import com.example.granjaavicola.data.entity.FaseEstandar
import com.example.granjaavicola.data.entity.MedioMovimientoBancario
import com.example.granjaavicola.data.entity.ProductoVenta
import com.example.granjaavicola.data.entity.RegimenTributario
import com.example.granjaavicola.data.entity.TipoDocumento
import com.example.granjaavicola.data.entity.TipoMovimientoBancario
import com.example.granjaavicola.data.entity.TipoMovimientoInventario
import com.example.granjaavicola.data.entity.TipoPago
import com.example.granjaavicola.data.entity.TipoPersona
import com.example.granjaavicola.data.entity.TipoReferenciaPago
import com.example.granjaavicola.data.entity.TipoSanidad
import com.example.granjaavicola.data.entity.TipoSoporte
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
    fun fromTipoSoporte(value: String?): TipoSoporte? {
        return value?.let { enumValueOf<TipoSoporte>(it) }
    }

    @TypeConverter
    fun toTipoSoporte(tipo: TipoSoporte?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromFaseConcepto(value: String?): FaseConcepto? {
        return value?.let { enumValueOf<FaseConcepto>(it) }
    }

    @TypeConverter
    fun toFaseConcepto(fase: FaseConcepto?): String? {
        return fase?.name
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

    @TypeConverter
    fun fromTipoDocumento(value: String?): TipoDocumento? {
        return value?.let { enumValueOf<TipoDocumento>(it) }
    }

    @TypeConverter
    fun toTipoDocumento(tipo: TipoDocumento?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromTipoMovimientoBancario(value: String?): TipoMovimientoBancario? {
        return value?.let { enumValueOf<TipoMovimientoBancario>(it) }
    }

    @TypeConverter
    fun toTipoMovimientoBancario(tipo: TipoMovimientoBancario?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromMedioMovimientoBancario(value: String?): MedioMovimientoBancario? {
        return value?.let { enumValueOf<MedioMovimientoBancario>(it) }
    }

    @TypeConverter
    fun toMedioMovimientoBancario(medio: MedioMovimientoBancario?): String? {
        return medio?.name
    }

    @TypeConverter
    fun fromTipoPersona(value: String?): TipoPersona? {
        return value?.let { enumValueOf<TipoPersona>(it) }
    }

    @TypeConverter
    fun toTipoPersona(tipo: TipoPersona?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromRegimenTributario(value: String?): RegimenTributario? {
        return value?.let { enumValueOf<RegimenTributario>(it) }
    }

    @TypeConverter
    fun toRegimenTributario(regimen: RegimenTributario?): String? {
        return regimen?.name
    }
}
