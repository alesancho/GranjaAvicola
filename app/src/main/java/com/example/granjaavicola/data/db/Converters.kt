package com.example.granjaavicola.data.db

import androidx.room.TypeConverter
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.data.entity.FaseEstandar
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
}
