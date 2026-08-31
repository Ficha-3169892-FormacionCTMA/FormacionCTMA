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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import com.miguelloaiza.miformacionctma.ui.Semana3Screen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.miguelloaiza.miformacionctma.viewmodel.ActividadesViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lista de actividades utilizada para conservar
        // el resumen de la Semana 2.
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

                    // El ViewModel administra las actividades
                    // que serán mostradas en la pantalla.
                    val actividadesViewModel: ActividadesViewModel = viewModel()

                    Semana3Screen(
                        actividades = actividadesViewModel.uiState.value.actividades,
                        resumenSemana2 = resumen
                    )
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

private fun actividadesSemana3(): List<ActividadFormativa> = listOf(
    ActividadFormativa(
        101L,
        "Kotlin básico",
        "Fundamentos de Kotlin",
        0,
        5,
        Prioridad.ALTA
    ),
    ActividadFormativa(
        102L,
        "Configurar Android Studio",
        "Preparación del entorno",
        100,
        -2,
        Prioridad.MEDIA
    ),
    ActividadFormativa(
        103L,
        "Ejercicios de colecciones",
        "Listas, filtros y operaciones",
        40,
        5,
        Prioridad.BAJA
    ),
    ActividadFormativa(
        104L,
        "Jetpack Compose",
        "Primeros composables",
        60,
        2,
        Prioridad.ALTA
    ),
    ActividadFormativa(
        105L,
        "Modifiers",
        "Orden y comportamiento de Modifier",
        80,
        1,
        Prioridad.MEDIA
    ),
    ActividadFormativa(
        106L,
        "Layouts",
        "Column, Row y Box",
        20,
        7,
        Prioridad.BAJA
    ),
    ActividadFormativa(
        107L,
        "Material 3",
        "Tema, color y tipografía",
        100,
        0,
        Prioridad.ALTA
    ),
    ActividadFormativa(
        108L,
        "Accesibilidad",
        "Semántica y texto escalable",
        30,
        3,
        Prioridad.MEDIA
    ),
    ActividadFormativa(
        109L,
        "LazyColumn",
        "Lista de actividades",
        70,
        4,
        Prioridad.BAJA
    ),
    ActividadFormativa(
        110L,
        "Grid adaptable",
        "Diseño para ancho ampliado",
        50,
        6,
        Prioridad.MEDIA
    )
)