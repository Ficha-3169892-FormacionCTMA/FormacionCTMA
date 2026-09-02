package com.miguelloaiza.miformacionctma.data.repository

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad

class InMemoryActividadRepository : ActividadRepository {

    private val actividades = mutableListOf(
        ActividadFormativa(
            id = 1L,
            titulo = "Actividad completada",
            descripcion = "Actividad finalizada",
            progreso = 100,
            diasRestantes = -2,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 2L,
            titulo = "Actividad pendiente",
            descripcion = "Actividad pendiente de realizar",
            progreso = 0,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 3L,
            titulo = "Actividad en proceso",
            descripcion = "Actividad actualmente en desarrollo",
            progreso = 60,
            diasRestantes = 2,
            prioridad = Prioridad.MEDIA
        )
    )

    override fun obtenerActividades(): List<ActividadFormativa> {
        return actividades.toList()
    }

    override fun obtenerActividad(id: Long): ActividadFormativa? {
        return actividades.find { it.id == id }
    }

    override fun eliminarActividad(id: Long): ActividadFormativa? {
        val actividad = actividades.find { it.id == id }

        if (actividad != null) {
            actividades.remove(actividad)
        }

        return actividad
    }

    override fun restaurarActividad(actividad: ActividadFormativa) {
        if (actividades.none { it.id == actividad.id }) {
            actividades.add(actividad)
        }
    }
}