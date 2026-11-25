package com.example.granjaavicola.ui.produccion

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.granjaavicola.data.entity.EstadoLote
import com.example.granjaavicola.ui.levante.DialogoFecha
import com.example.granjaavicola.util.formatearFecha
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onIrALotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandidoLote by rememberSaveable { mutableStateOf(false) }
    var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(estado.mensajeSnackbar) {
        estado.mensajeSnackbar?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarSnackbar()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro diario",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->

        if (estado.cargandoLotes) {
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

        if (estado.lotesActivos.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Primero crea un lote",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onIrALotes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp)
                    ) {
                        Text(
                            text = "Ir a Lotes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Selector de lote activo ───────────────────────────────────────
            item {
                val loteActual = estado.loteSeleccionado
                ExposedDropdownMenuBox(
                    expanded = expandidoLote,
                    onExpandedChange = { expandidoLote = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        readOnly = true,
                        value = loteActual?.let {
                            val etapa = if (it.estado == EstadoLote.PRODUCCION) "Producción" else "Levante"
                            "Lote ${it.numeroLote} ($etapa)"
                        } ?: "",
                        onValueChange = {},
                        label = { Text("Lote activo *", style = MaterialTheme.typography.bodyLarge) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoLote) },
                        isError = estado.errorLote != null,
                        supportingText = estado.errorLote?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoLote,
                        onDismissRequest = { expandidoLote = false }
                    ) {
                        estado.lotesActivos.forEach { lote ->
                            val etapa = if (lote.estado == EstadoLote.PRODUCCION) "Producción" else "Levante"
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Lote ${lote.numeroLote} ($etapa)",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    viewModel.seleccionarLote(lote.id)
                                    expandidoLote = false
                                }
                            )
                        }
                    }
                }
            }

            if (estado.loteSeleccionado == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "Selecciona un lote para registrar los datos del día.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
                return@LazyColumn
            }

            val lote = estado.loteSeleccionado!!

            // ── Fecha y accesos rápidos Ayer / Hoy ────────────────────────────
            item {
                val hoy = LocalDate.now()
                val ayer = hoy.minusDays(1)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SelectorFechaRegistro(
                        fecha = estado.fecha,
                        error = estado.errorFecha,
                        onClick = { mostrarDatePicker = true }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (estado.fecha == ayer) {
                            Button(
                                onClick = viewModel::seleccionarAyer,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp)
                            ) {
                                Text("Ayer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = viewModel::seleccionarAyer,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp)
                            ) {
                                Text("Ayer", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        if (estado.fecha == hoy) {
                            Button(
                                onClick = viewModel::seleccionarHoy,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp)
                            ) {
                                Text("Hoy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = viewModel::seleccionarHoy,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 56.dp)
                            ) {
                                Text("Hoy", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }

            // ── Resumen en vivo: Aves vivas y g/ave/día ──────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Aves vivas",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${estado.avesVivas}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Consumo por ave",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${formatearDecimal1(estado.consumoGramosAveDia)} g/ave/día",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (estado.semanaEdadLote > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                            val estandar = estado.estandarSemana
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Estándar sem. ${estado.semanaEdadLote} · ${estado.lineaActiva.etiqueta}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (estandar == null) {
                                    Text(
                                        text = "Sin estándar registrado para esta semana",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    val consumoEsperado = estandar.consumoGAveDia?.let {
                                        "${formatearDecimal1(it)} g/ave/día"
                                    } ?: "—"
                                    Text(
                                        text = "Consumo esperado: $consumoEsperado",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    if (lote.estado == EstadoLote.PRODUCCION) {
                                        val posturaEsperada = estandar.posturaPct?.let {
                                            "${formatearDecimal1(it)}%"
                                        } ?: "—"
                                        val pesoHuevoEsperado = estandar.pesoHuevoG?.let {
                                            "${formatearDecimal1(it)} g"
                                        } ?: "—"
                                        Text(
                                            text = "Postura esperada: $posturaEsperada · Peso huevo: $pesoHuevoEsperado",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Campos principales ────────────────────────────────────────────
            item {
                CampoRegistro(
                    valor = estado.consumoKg,
                    etiqueta = "Consumo de alimento (kg) *",
                    error = estado.errorConsumoKg,
                    onCambio = viewModel::actualizarConsumoKg,
                    keyboardType = KeyboardType.Decimal
                )
            }

            item {
                CampoRegistro(
                    valor = estado.mortalidad,
                    etiqueta = "Mortalidad (aves)",
                    error = estado.errorMortalidad,
                    onCambio = viewModel::actualizarMortalidad,
                    keyboardType = KeyboardType.Number
                )
            }

            item {
                CampoRegistro(
                    valor = estado.descarte,
                    etiqueta = "Descarte (aves)",
                    error = estado.errorDescarte,
                    onCambio = viewModel::actualizarDescarte,
                    keyboardType = KeyboardType.Number
                )
            }

            // ── Sección de huevos (solo en PRODUCCION) ────────────────────────
            if (lote.estado == EstadoLote.PRODUCCION) {
                item {
                    Text(
                        text = "Producción de huevos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CampoRegistro(
                            valor = estado.huevosAA,
                            etiqueta = "Huevos AA",
                            error = estado.errorHuevosAA,
                            onCambio = viewModel::actualizarHuevosAA,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        CampoRegistro(
                            valor = estado.huevosA,
                            etiqueta = "Huevos A",
                            error = estado.errorHuevosA,
                            onCambio = viewModel::actualizarHuevosA,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CampoRegistro(
                            valor = estado.huevosB,
                            etiqueta = "Huevos B",
                            error = estado.errorHuevosB,
                            onCambio = viewModel::actualizarHuevosB,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        CampoRegistro(
                            valor = estado.huevosC,
                            etiqueta = "Huevos C",
                            error = estado.errorHuevosC,
                            onCambio = viewModel::actualizarHuevosC,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CampoRegistro(
                            valor = estado.huevosRotos,
                            etiqueta = "Huevos rotos",
                            error = estado.errorHuevosRotos,
                            onCambio = viewModel::actualizarHuevosRotos,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        CampoRegistro(
                            valor = estado.huevosSucios,
                            etiqueta = "Huevos sucios",
                            error = estado.errorHuevosSucios,
                            onCambio = viewModel::actualizarHuevosSucios,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total huevos",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "${estado.totalHuevos}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "% de postura",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "${formatearDecimal1(estado.porcentajePostura)}%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // ── Campos opcionales ─────────────────────────────────────────────
            item {
                Text(
                    text = "Datos adicionales (opcionales)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CampoRegistro(
                    valor = estado.aguaLitros,
                    etiqueta = "Agua (litros)",
                    error = estado.errorAguaLitros,
                    onCambio = viewModel::actualizarAguaLitros,
                    keyboardType = KeyboardType.Decimal
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CampoRegistro(
                        valor = estado.temperaturaMax,
                        etiqueta = "Temp. máx (°C)",
                        error = estado.errorTemperaturaMax,
                        onCambio = viewModel::actualizarTemperaturaMax,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    CampoRegistro(
                        valor = estado.temperaturaMin,
                        etiqueta = "Temp. mín (°C)",
                        error = estado.errorTemperaturaMin,
                        onCambio = viewModel::actualizarTemperaturaMin,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                CampoRegistro(
                    valor = estado.horasLuz,
                    etiqueta = "Horas de luz",
                    error = estado.errorHorasLuz,
                    onCambio = viewModel::actualizarHorasLuz,
                    keyboardType = KeyboardType.Decimal
                )
            }

            item {
                OutlinedTextField(
                    value = estado.observaciones,
                    onValueChange = viewModel::actualizarObservaciones,
                    label = { Text("Observaciones", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    minLines = 2,
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            // ── Error general ─────────────────────────────────────────────────
            estado.errorGeneral?.let { mensajeError ->
                item {
                    Text(
                        text = mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // ── Botón Guardar / Actualizar ────────────────────────────────────
            item {
                Button(
                    onClick = viewModel::guardarRegistro,
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
                            text = if (estado.esEdicion) "Actualizar" else "Guardar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (mostrarDatePicker) {
        DialogoFecha(
            titulo = "Fecha del registro",
            fechaInicial = estado.fecha,
            onConfirmar = { fecha ->
                viewModel.seleccionarFecha(fecha)
                mostrarDatePicker = false
            },
            onCancelar = { mostrarDatePicker = false }
        )
    }
}

@Composable
private fun SelectorFechaRegistro(
    fecha: LocalDate,
    error: String?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClick()
            }
        }
    }

    OutlinedTextField(
        value = formatearFecha(fecha),
        onValueChange = {},
        readOnly = true,
        interactionSource = interactionSource,
        label = { Text("Fecha *", style = MaterialTheme.typography.bodyLarge) },
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

@Composable
private fun CampoRegistro(
    valor: String,
    etiqueta: String,
    error: String?,
    onCambio: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyLarge) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

private fun formatearDecimal1(valor: Double): String =
    String.format(Locale.forLanguageTag("es-CO"), "%.1f", valor)
