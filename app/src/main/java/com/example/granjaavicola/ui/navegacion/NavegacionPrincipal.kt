package com.example.granjaavicola.ui.navegacion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.granjaavicola.ui.finanzas.FinanzasScreen
import com.example.granjaavicola.ui.informes.InformesScreen
import com.example.granjaavicola.ui.inicio.InicioScreen
import com.example.granjaavicola.ui.levante.LotesScreen
import com.example.granjaavicola.ui.produccion.RegistroScreen

// Pantalla contenedora principal con barra de navegación inferior
@Composable
fun NavegacionPrincipal() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                DestinoNavegacion.entries.forEach { destino ->
                    val seleccionado = rutaActual == destino.ruta
                    NavigationBarItem(
                        selected = seleccionado,
                        onClick = {
                            if (!seleccionado) {
                                navController.navigate(destino.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destino.icono,
                                contentDescription = destino.titulo
                            )
                        },
                        label = {
                            Text(text = destino.titulo)
                        }
                    )
                }
            }
        }
    ) { paddingValores ->
        NavHost(
            navController = navController,
            startDestination = DestinoNavegacion.INICIO.ruta,
            modifier = Modifier.padding(paddingValores)
        ) {
            composable(DestinoNavegacion.INICIO.ruta) {
                InicioScreen()
            }
            composable(DestinoNavegacion.REGISTRO.ruta) {
                RegistroScreen()
            }
            composable(DestinoNavegacion.LOTES.ruta) {
                LotesScreen()
            }
            composable(DestinoNavegacion.FINANZAS.ruta) {
                FinanzasScreen()
            }
            composable(DestinoNavegacion.INFORMES.ruta) {
                InformesScreen()
            }
        }
    }
}
