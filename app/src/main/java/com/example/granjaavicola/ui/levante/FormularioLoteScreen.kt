package com.example.granjaavicola.ui.levante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.granjaavicola.util.formatearFecha
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioLoteScreen(
    viewModel: LotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.formularioState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.reiniciarFormulario()
    }

    // Navegar atrás cuando se guarda exitosamente
    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            viewModel.reiniciarFormulario()
            onNavigateBack()
        }
    }

    var mostrarDatePickerNacimiento by rememberSaveable { mutableStateOf(false) }
    var mostrarDatePickerLlegada by rememberSaveable { mutableStateOf(false) }
    var expandidoGalpon by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo lote",
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

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Número de lote ────────────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.numeroLote,
                    etiqueta = "Número de lote *",
                    error = estado.errorNumeroLote,
                    onCambio = viewModel::actualizarNumeroLote,
                    keyboardType = KeyboardType.Text
                )
            }

            // ── Galpón ────────────────────────────────────────────────────────
            item {
                val galponSeleccionado = estado.galpones.firstOrNull { it.id == estado.galponId }
                ExposedDropdownMenuBox(
                    expanded = expandidoGalpon,
                    onExpandedChange = { expandidoGalpon = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        readOnly = true,
                        value = galponSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        label = { Text("Galpón *", style = MaterialTheme.typography.bodyLarge) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoGalpon) },
                        isError = estado.errorGalpon != null,
                        supportingText = estado.errorGalpon?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoGalpon,
                        onDismissRequest = { expandidoGalpon = false }
                    ) {
                        estado.galpones.forEach { galpon ->
                            DropdownMenuItem(
                                text = { Text("${galpon.nombre} (cap. ${galpon.capacidad})", style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    viewModel.seleccionarGalpon(galpon.id)
                                    expandidoGalpon = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "+ Crear galpón nuevo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                viewModel.mostrarNuevoGalpon(true)
                                expandidoGalpon = false
                            }
                        )
                    }
                }
            }

            // ── Formulario nuevo galpón (inline) ──────────────────────────────
            if (estado.mostrandoNuevoGalpon) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Nuevo galpón",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        CampoTexto(
                            valor = estado.nuevoGalponNombre,
                            etiqueta = "Nombre del galpón *",
                            error = estado.errorNuevoGalponNombre,
                            onCambio = viewModel::actualizarNuevoGalponNombre
                        )
                        CampoTexto(
                            valor = estado.nuevoGalponCapacidad,
                            etiqueta = "Capacidad (aves) *",
                            error = estado.errorNuevoGalponCapacidad,
                            onCambio = viewModel::actualizarNuevoGalponCapacidad,
                            keyboardType = KeyboardType.Number
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp),
                                onClick = { viewModel.mostrarNuevoGalpon(false) }
                            ) {
                                Text("Cancelar", style = MaterialTheme.typography.titleMedium)
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp),
                                onClick = viewModel::guardarNuevoGalpon
                            ) {
                                Text("Guardar galpón", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }

            // ── Fecha de nacimiento ───────────────────────────────────────────
            item {
                SelectorFecha(
                    etiqueta = "Fecha de nacimiento *",
                    fecha = estado.fechaNacimiento,
                    error = estado.errorFechaNacimiento,
                    onClick = { mostrarDatePickerNacimiento = true }
                )
            }

            // ── Fecha de llegada ──────────────────────────────────────────────
            item {
                SelectorFecha(
                    etiqueta = "Fecha de llegada *",
                    fecha = estado.fechaLlegada,
                    error = estado.errorFechaLlegada,
                    onClick = { mostrarDatePickerLlegada = true }
                )
            }

            // ── Número de aves ────────────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.avesIniciales,
                    etiqueta = "Número de aves *",
                    error = estado.errorAves,
                    onCambio = viewModel::actualizarAves,
                    keyboardType = KeyboardType.Number
                )
            }

            // ── Peso inicial (gramos) ─────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.pesoInicialG,
                    etiqueta = "Peso inicial promedio (g) *",
                    error = estado.errorPeso,
                    onCambio = viewModel::actualizarPeso,
                    keyboardType = KeyboardType.Decimal
                )
            }

            // ── Línea genética ────────────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.lineaGenetica,
                    etiqueta = "Línea genética",
                    error = null,
                    onCambio = viewModel::actualizarLineaGenetica
                )
            }

            // ── Proveedor ────────────────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.proveedor,
                    etiqueta = "Proveedor",
                    error = null,
                    onCambio = viewModel::actualizarProveedor
                )
            }

            // ── Costo por pollita ─────────────────────────────────────────────
            item {
                CampoTexto(
                    valor = estado.costoPorPollita,
                    etiqueta = "Costo por pollita (COP) *",
                    error = estado.errorCosto,
                    onCambio = viewModel::actualizarCosto,
                    keyboardType = KeyboardType.Number,
                    prefijo = "$"
                )
            }

            // ── Error general ─────────────────────────────────────────────────
            if (estado.errorGeneral != null) {
                item {
                    Text(
                        text = estado.errorGeneral ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // ── Botón guardar ────────────────────────────────────────────────
            item {
                Button(
                    onClick = viewModel::guardarLote,
                    enabled = !estado.guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (estado.guardando) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Guardar lote",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // ── DatePicker nacimiento ─────────────────────────────────────────────────
    if (mostrarDatePickerNacimiento) {
        DialogoFecha(
            titulo = "Fecha de nacimiento",
            fechaInicial = estado.fechaNacimiento,
            onConfirmar = { fecha ->
                viewModel.actualizarFechaNacimiento(fecha)
                mostrarDatePickerNacimiento = false
            },
            onCancelar = { mostrarDatePickerNacimiento = false }
        )
    }

    // ── DatePicker llegada ────────────────────────────────────────────────────
    if (mostrarDatePickerLlegada) {
        DialogoFecha(
            titulo = "Fecha de llegada",
            fechaInicial = estado.fechaLlegada,
            onConfirmar = { fecha ->
                viewModel.actualizarFechaLlegada(fecha)
                mostrarDatePickerLlegada = false
            },
            onCancelar = { mostrarDatePickerLlegada = false }
        )
    }
}

// ── Componentes reutilizables ─────────────────────────────────────────────────

@Composable
private fun CampoTexto(
    valor: String,
    etiqueta: String,
    error: String?,
    onCambio: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        prefix = prefijo?.let { { Text(it) } },
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun SelectorFecha(
    etiqueta: String,
    fecha: LocalDate?,
    error: String?,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = fecha?.let { formatearFecha(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
            }
        },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoFecha(
    titulo: String,
    fechaInicial: LocalDate?,
    onConfirmar: (LocalDate) -> Unit,
    onCancelar: () -> Unit
) {
    val inicialMillis = fechaInicial
        ?.atStartOfDay(ZoneOffset.UTC)
        ?.toInstant()
        ?.toEpochMilli()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = inicialMillis)

    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    val seleccionadoMs = datePickerState.selectedDateMillis
                    if (seleccionadoMs != null) {
                        val fecha = Instant.ofEpochMilli(seleccionadoMs)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onConfirmar(fecha)
                    }
                }
            ) { Text("Aceptar", style = MaterialTheme.typography.titleMedium) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) {
        DatePicker(state = datePickerState, title = { Text(titulo, modifier = Modifier.padding(start = 24.dp, top = 16.dp)) })
    }
}
