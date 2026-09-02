package com.miguelloaiza.miformacionctma.data.repository

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad

class InMemoryActividadRepository : ActividadRepository {

    private val actividades = listOf(
        ActividadFormativa(
            id = 101L,
            titulo = "Kotlin básico",
            descripcion = "Fundamentos de Kotlin",
            progreso = 0,
            diasRestantes = 5,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 102L,
            titulo = "Configurar Android Studio",
            descripcion = "Preparación del entorno",
            progreso = 100,
            diasRestantes = -2,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 103L,
            titulo = "Ejercicios de colecciones",
            descripcion = "Listas, filtros y operaciones",
            progreso = 40,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 104L,
            titulo = "Jetpack Compose",
            descripcion = "Primeros composables",
            progreso = 60,
            diasRestantes = 2,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 105L,
            titulo = "Modifiers",
            descripcion = "Orden y comportamiento de Modifier",
            progreso = 80,
            diasRestantes = 1,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 106L,
            titulo = "Layouts",
            descripcion = "Column, Row y Box",
            progreso = 20,
            diasRestantes = 7,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 107L,
            titulo = "Material 3",
            descripcion = "Tema, color y tipografía",
            progreso = 100,
            diasRestantes = 0,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 108L,
            titulo = "Accesibilidad",
            descripcion = "Semántica y texto escalable",
            progreso = 30,
            diasRestantes = 3,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 109L,
            titulo = "LazyColumn",
            descripcion = "Lista de actividades",
            progreso = 70,
            diasRestantes = 4,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 110L,
            titulo = "Grid adaptable",
            descripcion = "Diseño para ancho ampliado",
            progreso = 50,
            diasRestantes = 6,
            prioridad = Prioridad.MEDIA
        )
    )

    override fun obtenerActividades(): List<ActividadFormativa> {
        return actividades
    }

    override fun obtenerActividad(id: Long): ActividadFormativa? {
        return actividades.find { it.id == id }
    }
}