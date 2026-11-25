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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.granjaavicola.GranjaApp
import com.example.granjaavicola.ui.ajustes.AjustesScreen
import com.example.granjaavicola.ui.ajustes.AjustesViewModel
import com.example.granjaavicola.ui.finanzas.FinanzasScreen
import com.example.granjaavicola.ui.informes.InformesScreen
import com.example.granjaavicola.ui.inicio.InicioScreen
import com.example.granjaavicola.ui.levante.DetalleLoteScreen
import com.example.granjaavicola.ui.levante.FormularioLoteScreen
import com.example.granjaavicola.ui.levante.LotesScreen
import com.example.granjaavicola.ui.levante.LotesViewModel
import com.example.granjaavicola.ui.produccion.RegistroScreen
import com.example.granjaavicola.ui.produccion.RegistroViewModel

// Rutas adicionales
private const val RUTA_NUEVO_LOTE = "nuevo_lote"
private const val RUTA_DETALLE_LOTE = "detalle_lote/{loteId}"
private const val RUTA_AJUSTES = "ajustes"
private fun rutaDetalleLote(id: Long) = "detalle_lote/$id"

// Pantalla contenedora principal con barra de navegación inferior
@Composable
fun NavegacionPrincipal() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    // Destinos de la barra inferior (sólo los que tienen ícono)
    val destinosBarraInferior = DestinoNavegacion.entries

    // Determinar si la barra inferior debe ocultarse (en sub-pantallas de lotes y ajustes)
    val mostrarBarraInferior = rutaActual != RUTA_NUEVO_LOTE &&
        rutaActual != RUTA_AJUSTES &&
        rutaActual?.startsWith("detalle_lote/") != true

    // Instanciar los ViewModels una sola vez para los módulos correspondientes
    val context = LocalContext.current
    val container = (context.applicationContext as GranjaApp).container
    val lotesViewModel: LotesViewModel = viewModel(
        factory = LotesViewModel.factory(
            loteRepository = container.loteRepository,
            galponRepository = container.galponRepository
        )
    )
    val registroViewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModel.factory(
            loteRepository = container.loteRepository,
            registroDiarioRepository = container.registroDiarioRepository,
            produccionHuevoRepository = container.produccionHuevoRepository,
            estandarRepository = container.estandarRepository,
            preferenciasRepository = container.preferenciasRepository,
            db = container.database
        )
    )
    val ajustesViewModel: AjustesViewModel = viewModel(
        factory = AjustesViewModel.factory(
            preferenciasRepository = container.preferenciasRepository
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
                    destinosBarraInferior.forEach { destino ->
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
        }
    ) { paddingValores ->
        NavHost(
            navController = navController,
            startDestination = DestinoNavegacion.INICIO.ruta,
            modifier = Modifier.padding(paddingValores)
        ) {
            composable(DestinoNavegacion.INICIO.ruta) {
                InicioScreen(
                    onIrAAjustes = { navController.navigate(RUTA_AJUSTES) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(RUTA_AJUSTES) {
                AjustesScreen(
                    viewModel = ajustesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(DestinoNavegacion.REGISTRO.ruta) {
                RegistroScreen(
                    viewModel = registroViewModel,
                    onIrALotes = {
                        navController.navigate(DestinoNavegacion.LOTES.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(DestinoNavegacion.LOTES.ruta) {
                LotesScreen(
                    viewModel = lotesViewModel,
                    onNuevoLote = { navController.navigate(RUTA_NUEVO_LOTE) },
                    onDetalleLote = { id -> navController.navigate(rutaDetalleLote(id)) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(RUTA_NUEVO_LOTE) {
                FormularioLoteScreen(
                    viewModel = lotesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(RUTA_DETALLE_LOTE) { backStackEntry ->
                val loteId = backStackEntry.arguments?.getString("loteId")?.toLongOrNull() ?: return@composable
                DetalleLoteScreen(
                    loteId = loteId,
                    viewModel = lotesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
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
