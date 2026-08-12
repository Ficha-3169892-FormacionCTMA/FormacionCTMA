package com.miguelloaiza.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lista de ejemplo solicitada por la integración mínima.
        val actividades = listOf(
            ActividadFormativa(
                id = 1L,
                titulo = "Kotlin básico",
                descripcion = "Fundamentos de Kotlin",
                progreso = 80,
                diasRestantes = 1,
                prioridad = Prioridad.ALTA
            ),
            ActividadFormativa(
                id = 2L,
                titulo = "Configurar Android Studio",
                descripcion = null,
                progreso = 100,
                diasRestantes = -2,
                prioridad = Prioridad.MEDIA
            ),
            ActividadFormativa(
                id = 3L,
                titulo = "Ejercicios de colecciones",
                descripcion = "Listas, filtros y operaciones",
                progreso = 40,
                diasRestantes = 5,
                prioridad = Prioridad.BAJA
            )
        )

        // La pantalla recibe el resultado calculado por las reglas.
        val resumen = ReglasActividad.resumen(actividades)

        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InicioScreen(resumen)
                }
            }
        }
    }
}

@Composable
private fun InicioScreen(resumen: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Resumen de actividades",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = resumen,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}