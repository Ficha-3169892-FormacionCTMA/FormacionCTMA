package com.miguelloaiza.miformacionctma.rules

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad

/**
 * Reglas de negocio de Mi Formación CTMA.
 *
 * Se mantienen separadas de la interfaz para que puedan probarse
 * y reutilizarse sin depender de Compose.
 */
object ReglasActividad {

    // Validar actividad: devuelve TODOS los errores encontrados.
    fun validarActividad(
        titulo: String,
        progreso: Int
    ): List<String> = buildList {
        if (titulo.isBlank()) {
            add("El título es obligatorio")
        }

        if (progreso !in 0..100) {
            add("El progreso debe estar entre 0 y 100")
        }
    }

    // Estado: distingue pendiente, en proceso, completada y vencida.
    fun estadoActividad(
        actividad: ActividadFormativa
    ): EstadoActividad = when {
        actividad.progreso == 100 -> EstadoActividad.COMPLETADA
        actividad.diasRestantes < 0 -> EstadoActividad.VENCIDA
        actividad.progreso == 0 -> EstadoActividad.PENDIENTE
        else -> EstadoActividad.EN_PROCESO
    }

    // Actividades no completadas con dos días o menos.
    fun actividadesUrgentes(
        actividades: List<ActividadFormativa>
    ): List<ActividadFormativa> =
        actividades.filter {
            estadoActividad(it) != EstadoActividad.COMPLETADA &&
                it.diasRestantes <= 2
        }

    // Trata correctamente una lista vacía.
    fun promedioProgreso(
        actividades: List<ActividadFormativa>
    ): Double =
        if (actividades.isEmpty()) {
            0.0
        } else {
            actividades.map { it.progreso }.average()
        }

    // Ignora mayúsculas/minúsculas y espacios externos.
    fun buscarPorTitulo(
        actividades: List<ActividadFormativa>,
        texto: String
    ): List<ActividadFormativa> {
        val consulta = texto.trim()

        if (consulta.isEmpty()) return emptyList()

        return actividades.filter {
            it.titulo.trim().contains(
                other = consulta,
                ignoreCase = true
            )
        }
    }

    // Ejemplo A de la guía: priorización de compromisos.
    data class Compromiso(
        val titulo: String,
        val diasRestantes: Int,
        val completado: Boolean
    )

    fun prioridad(compromiso: Compromiso): String = when {
        compromiso.completado -> "Finalizado"
        compromiso.diasRestantes < 0 -> "Vencido"
        compromiso.diasRestantes <= 2 -> "Urgente"
        else -> "Planificado"
    }

    // Ejemplo B de la guía: resumen de avance del grupo.
    fun resumenProgresos(progresos: List<Int>): String {
        if (progresos.isEmpty()) return "Sin datos"

        val validos = progresos.filter { it in 0..100 }

        if (validos.isEmpty()) return "Sin datos válidos"

        return "Promedio: %.1f%% · Completadas: %d".format(
            validos.average(),
            validos.count { it == 100 }
        )
    }

    // Ejemplo C de la guía: dato opcional.
    fun nombreVisible(
        nombreCompleto: String?,
        alias: String
    ): String =
        nombreCompleto
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: alias

    // Reto adicional:
    // 1. Vencidas primero.
    // 2. Luego prioridad alta.
    // 3. Finalmente menor número de días.
    fun ordenarActividades(
        actividades: List<ActividadFormativa>
    ): List<ActividadFormativa> =
        actividades.sortedWith(
            compareBy<ActividadFormativa> {
                estadoActividad(it) != EstadoActividad.VENCIDA
            }.thenByDescending {
                it.prioridad == Prioridad.ALTA
            }.thenBy {
                it.diasRestantes
            }
        )

    // Resumen que recibe la interfaz.
    fun resumen(actividades: List<ActividadFormativa>): String {
        if (actividades.isEmpty()) return "Sin datos"

        val promedio = promedioProgreso(actividades)

        val completadas = actividades.count {
            estadoActividad(it) == EstadoActividad.COMPLETADA
        }

        val urgentes = actividadesUrgentes(actividades).size

        return "Promedio: %.1f%% · Completadas: %d · Urgentes: %d".format(
            promedio,
            completadas,
            urgentes
        )
    }
}
