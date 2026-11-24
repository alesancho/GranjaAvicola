package com.example.granjaavicola.ui.navegacion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

// Destinos principales de la barra de navegación inferior
enum class DestinoNavegacion(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    INICIO("inicio", "Inicio", Icons.Default.Home),
    REGISTRO("registro", "Registro", Icons.Default.Edit),
    LOTES("lotes", "Lotes", Icons.Default.DateRange),
    FINANZAS("finanzas", "Finanzas", Icons.Default.ShoppingCart),
    INFORMES("informes", "Informes", Icons.Default.Info)
}
