package com.example.granjaavicola.ui.levante

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.util.formatearCOP
import com.example.granjaavicola.util.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLoteScreen(
    loteId: Long,
    viewModel: LotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(loteId) {
        viewModel.cargarLote(loteId)
    }

    val estado by viewModel.detalleState.collectAsStateWithLifecycle()

    // Volver cuando el lote se cierra exitosamente
    LaunchedEffect(estado.cerradoExitoso) {
        if (estado.cerradoExitoso) onNavigateBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = estado.lote?.let { "Lote ${it.numeroLote}" } ?: "Detalle",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->

        if (estado.cargando) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val lote = estado.lote
        if (lote == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Lote no encontrado", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Tarjeta de estado ─────────────────────────────────────────────
            item {
                val etiquetaEstado = when (lote.estado) {
                    EstadoLote.LEVANTE -> "Levante"
                    EstadoLote.PRODUCCION -> "Producción"
                    EstadoLote.CERRADO -> "Cerrado"
                }
                val colorEstado = when (lote.estado) {
                    EstadoLote.LEVANTE -> MaterialTheme.colorScheme.tertiary
                    EstadoLote.PRODUCCION -> MaterialTheme.colorScheme.primary
                    EstadoLote.CERRADO -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Estado: $etiquetaEstado",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado
                        )
                        Text(
                            text = "Semana ${estado.edadSemanas} de edad",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // ── Datos del lote ────────────────────────────────────────────────
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Datos del lote",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider()
                        FilaDato("Número de lote", lote.numeroLote)
                        FilaDato("Galpón", estado.nombreGalpon)
                        FilaDato("Fecha de nacimiento", formatearFecha(lote.fechaNacimiento))
                        FilaDato("Fecha de llegada", formatearFecha(lote.fechaLlegada))
                        FilaDato("Aves iniciales", "${lote.avesIniciales}")
                        FilaDato("Peso inicial promedio", "${lote.pesoInicialG} g")
                        if (lote.lineaGenetica.isNotBlank()) {
                            FilaDato("Línea genética", lote.lineaGenetica)
                        }
                        if (lote.proveedor.isNotBlank()) {
                            FilaDato("Proveedor", lote.proveedor)
                        }
                        FilaDato("Costo por pollita", formatearCOP(lote.costoPorPollita))
                        if (lote.fechaTraslado != null) {
                            FilaDato("Fecha de traslado", formatearFecha(lote.fechaTraslado))
                        }
                        if (lote.fechaCierre != null) {
                            FilaDato("Fecha de cierre", formatearFecha(lote.fechaCierre))
                        }
                    }
                }
            }

            // ── Error de cierre ───────────────────────────────────────────────
            estado.errorCierre?.let { mensajeError ->
                item {
                    Text(
                        text = mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // ── Botón trasladar a producción (solo LEVANTE) ───────────────────
            if (lote.estado == EstadoLote.LEVANTE) {
                item {
                    Button(
                        onClick = { viewModel.mostrarDialogoTraslado(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Trasladar a producción",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Botón cerrar lote ─────────────────────────────────────────────
            if (lote.estado != EstadoLote.CERRADO) {
                item {
                    Button(
                        onClick = { viewModel.mostrarConfirmacionCierre(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(
                            text = "Cerrar lote",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // ── Diálogo de confirmación de cierre ─────────────────────────────────────
    if (estado.mostrandoConfirmacionCierre) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarConfirmacionCierre(false) },
            title = {
                Text(
                    "¿Cerrar lote?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Esta acción marcará el lote como Cerrado con la fecha de hoy. El lote no se eliminará y no podrá reabrirse.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = viewModel::cerrarLote,
                    modifier = Modifier.defaultMinSize(minHeight = 56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, cerrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.mostrarConfirmacionCierre(false) },
                    modifier = Modifier.defaultMinSize(minHeight = 56.dp)
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.titleMedium)
                }
            }
        )
    }

    // ── Diálogo de traslado a producción ──────────────────────────────────────
    var mostrarDatePickerTraslado by rememberSaveable { mutableStateOf(false) }

    if (estado.mostrandoDialogoTraslado) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    mostrarDatePickerTraslado = true
                }
            }
        }

        AlertDialog(
            onDismissRequest = { viewModel.mostrarDialogoTraslado(false) },
            title = {
                Text(
                    "Trasladar a producción",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Selecciona la fecha en que el lote inicia producción.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = formatearFecha(estado.fechaTrasladoSeleccionada),
                        onValueChange = {},
                        readOnly = true,
                        interactionSource = interactionSource,
                        label = { Text("Fecha de traslado", style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        trailingIcon = {
                            IconButton(onClick = { mostrarDatePickerTraslado = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha de traslado")
                            }
                        },
                        isError = estado.errorTraslado != null,
                        supportingText = estado.errorTraslado?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmarTrasladoAProduccion,
                    modifier = Modifier.defaultMinSize(minHeight = 56.dp)
                ) {
                    Text("Confirmar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.mostrarDialogoTraslado(false) },
                    modifier = Modifier.defaultMinSize(minHeight = 56.dp)
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.titleMedium)
                }
            }
        )
    }

    if (mostrarDatePickerTraslado) {
        DialogoFecha(
            titulo = "Fecha de traslado",
            fechaInicial = estado.fechaTrasladoSeleccionada,
            onConfirmar = { fecha ->
                viewModel.actualizarFechaTraslado(fecha)
                mostrarDatePickerTraslado = false
            },
            onCancelar = { mostrarDatePickerTraslado = false }
        )
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
