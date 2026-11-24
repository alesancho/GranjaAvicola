package com.example.granjaavicola.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Esquema de color en modo claro con paleta verde y ámbar
private val ColorSchemeClaro = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = VerdeSobrePrimario,
    primaryContainer = VerdeContenedorPrimario,
    onPrimaryContainer = VerdeSobreContenedorPrimario,
    secondary = AmbarSecundario,
    onSecondary = AmbarSobreSecundario,
    secondaryContainer = AmbarContenedorSecundario,
    onSecondaryContainer = AmbarSobreContenedorSecundario,
    tertiary = TerciarioTierra,
    onTertiary = SobreTerciarioTierra,
    tertiaryContainer = ContenedorTerciarioTierra,
    onTertiaryContainer = SobreContenedorTerciarioTierra,
    background = FondoClaro,
    onBackground = SobreFondoClaro,
    surface = SuperficieClara,
    onSurface = SobreSuperficieClara,
    surfaceVariant = VarianteSuperficieClara,
    onSurfaceVariant = SobreVarianteSuperficieClara,
    outline = BordeClaro
)

// Tema Material 3 exclusivo en modo claro con tipografía de alta legibilidad
@Composable
fun GranjaAvicolaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorSchemeClaro,
        typography = Typography,
        content = content
    )
}
