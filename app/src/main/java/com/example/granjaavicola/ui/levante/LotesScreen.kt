package com.example.granjaavicola.ui.levante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.util.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesScreen(
    viewModel: LotesViewModel,
    onNuevoLote: () -> Unit,
    onDetalleLote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.listaUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lotes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoLote,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo lote"
                    )
                },
                text = {
                    Text(
                        text = "Nuevo lote",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ── Filtros ──────────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FiltroEstado.entries) { filtro ->
                    FilterChip(
                        selected = uiState.filtroActivo == filtro,
                        onClick = { viewModel.cambiarFiltro(filtro) },
                        label = {
                            Text(
                                text = filtro.etiqueta,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }

            // ── Lista ────────────────────────────────────────────────────────
            if (uiState.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay lotes registrados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items, key = { it.lote.id }) { item ->
                        TarjetaLote(
                            item = item,
                            onClick = { onDetalleLote(item.lote.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) } // espacio FAB
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaLote(
    item: ItemLote,
    onClick: () -> Unit
) {
    val etiquetaEstado = when (item.lote.estado) {
        EstadoLote.LEVANTE -> "Levante"
        EstadoLote.PRODUCCION -> "Producción"
        EstadoLote.CERRADO -> "Cerrado"
    }
    val colorEstado = when (item.lote.estado) {
        EstadoLote.LEVANTE -> MaterialTheme.colorScheme.tertiary
        EstadoLote.PRODUCCION -> MaterialTheme.colorScheme.primary
        EstadoLote.CERRADO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lote ${item.lote.numeroLote}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = etiquetaEstado,
                    style = MaterialTheme.typography.labelLarge,
                    color = colorEstado,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Galpón: ${item.nombreGalpon}",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DatoLote(etiqueta = "Edad", valor = "${item.edadSemanas} sem")
                DatoLote(etiqueta = "Aves", valor = "${item.lote.avesIniciales}")
                DatoLote(etiqueta = "Llegada", valor = formatearFecha(item.lote.fechaLlegada))
            }
        }
    }
}

@Composable
private fun DatoLote(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
