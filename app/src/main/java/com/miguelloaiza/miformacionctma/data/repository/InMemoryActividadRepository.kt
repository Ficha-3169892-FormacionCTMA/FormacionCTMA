package com.miguelloaiza.miformacionctma.data.repository

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad

class InMemoryActividadRepository : ActividadRepository {

    private val actividades = listOf(
        ActividadFormativa(
            id = 1L,
            titulo = "Introducción a Kotlin",
            descripcion = "Conceptos básicos de Kotlin",
            progreso = 60,
            diasRestantes = 5,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 2L,
            titulo = "Jetpack Compose",
            descripcion = "Diseño de interfaces con Compose",
            progreso = 80,
            diasRestantes = 2,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 3L,
            titulo = "Pruebas de software",
            descripcion = "Pruebas unitarias e instrumentadas",
            progreso = 100,
            diasRestantes = -1,
            prioridad = Prioridad.ALTA
        )
    )

    override fun obtenerActividades(): List<ActividadFormativa> {
        return actividades
    }

    override fun obtenerActividad(id: Long): ActividadFormativa? {
        return actividades.find { it.id == id }
    }
}