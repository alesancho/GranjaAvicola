package com.example.granjaavicola.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.granjaavicola.data.preferences.PreferenciasRepository
import com.example.granjaavicola.domain.modelos.LineaEstandar
import kotlinx.coroutines.flow.StateFlow

class AjustesViewModel(
    private val preferenciasRepository: PreferenciasRepository
) : ViewModel() {

    val lineaActiva: StateFlow<LineaEstandar> = preferenciasRepository.lineaActiva

    fun seleccionarLinea(linea: LineaEstandar) {
        preferenciasRepository.establecerLineaActiva(linea)
    }

    companion object {
        fun factory(preferenciasRepository: PreferenciasRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AjustesViewModel(preferenciasRepository) as T
            }
    }
}
