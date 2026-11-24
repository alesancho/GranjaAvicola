package com.example.granjaavicola

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.granjaavicola.ui.navegacion.NavegacionPrincipal
import com.example.granjaavicola.ui.tema.GranjaAvicolaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GranjaAvicolaTheme {
                NavegacionPrincipal()
            }
        }
    }
}
