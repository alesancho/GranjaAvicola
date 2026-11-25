package com.example.granjaavicola.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.granjaavicola.domain.modelos.LineaEstandar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenciasRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(NOMBRE_ARCHIVO_PREFS, Context.MODE_PRIVATE)

    private val _lineaActiva = MutableStateFlow(leerLineaActiva())
    val lineaActiva: StateFlow<LineaEstandar> = _lineaActiva.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == CLAVE_LINEA_ACTIVA) {
            _lineaActiva.value = leerLineaActiva()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun obtenerLineaActiva(): LineaEstandar = leerLineaActiva()

    fun establecerLineaActiva(linea: LineaEstandar) {
        prefs.edit {
            putString(CLAVE_LINEA_ACTIVA, linea.codigo)
        }
        _lineaActiva.value = linea
    }

    private fun leerLineaActiva(): LineaEstandar {
        val codigo = prefs.getString(CLAVE_LINEA_ACTIVA, LineaEstandar.POR_DEFECTO.codigo)
        return LineaEstandar.desdeCodigo(codigo)
    }

    companion object {
        private const val NOMBRE_ARCHIVO_PREFS = "granja_avicola_prefs"
        const val CLAVE_LINEA_ACTIVA = "linea_activa"
    }
}
