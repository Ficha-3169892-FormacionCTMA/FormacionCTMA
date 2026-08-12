package com.miguelloaiza.miformacionctma.rules

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import java.util.Locale

/**
 * Reglas de negocio de Mi Formación CTMA.
 *
 * Las reglas se mantienen separadas de la interfaz
 * para poder probarlas y reutilizarlas.
 */
object ReglasActividad {

    // =========================================================
    // VALIDACIÓN
    // =========================================================

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


    // =========================================================
    // ESTADO DE LA ACTIVIDAD
    // =========================================================

    fun estadoActividad(
        actividad: ActividadFormativa
    ): EstadoActividad = when {

        actividad.progreso == 100 ->
            EstadoActividad.COMPLETADA

        actividad.diasRestantes < 0 ->
            EstadoActividad.VENCIDA

        actividad.progreso == 0 ->
            EstadoActividad.PENDIENTE

        else ->
            EstadoActividad.EN_PROCESO
    }


    // =========================================================
    // ACTIVIDADES URGENTES
    // =========================================================

    fun actividadesUrgentes(
        actividades: List<ActividadFormativa>
    ): List<ActividadFormativa> =
        actividades.filter {

            estadoActividad(it) != EstadoActividad.COMPLETADA &&
                    it.diasRestantes <= 2
        }


    // =========================================================
    // PROMEDIO DE PROGRESO
    // =========================================================

    fun promedioProgreso(
        actividades: List<ActividadFormativa>
    ): Double =
        if (actividades.isEmpty()) {
            0.0
        } else {
            actividades
                .map { it.progreso }
                .average()
        }


    // =========================================================
    // BÚSQUEDA POR TÍTULO
    // =========================================================

    fun buscarPorTitulo(
        actividades: List<ActividadFormativa>,
        texto: String
    ): List<ActividadFormativa> {

        val consulta = texto.trim()

        if (consulta.isEmpty()) {
            return emptyList()
        }

        return actividades.filter {
            it.titulo.trim().contains(
                other = consulta,
                ignoreCase = true
            )
        }
    }


    // =========================================================
    // EJEMPLO A - PRIORIDAD DE COMPROMISOS
    // =========================================================

    data class Compromiso(
        val titulo: String,
        val diasRestantes: Int,
        val completado: Boolean
    )

    fun prioridad(
        compromiso: Compromiso
    ): String = when {

        compromiso.completado ->
            "Finalizado"

        compromiso.diasRestantes < 0 ->
            "Vencido"

        compromiso.diasRestantes <= 2 ->
            "Urgente"

        else ->
            "Planificado"
    }


    // =========================================================
    // EJEMPLO B - RESUMEN DE PROGRESOS
    // =========================================================

    fun resumenProgresos(
        progresos: List<Int>
    ): String {

        if (progresos.isEmpty()) {
            return "Sin datos"
        }

        val validos = progresos.filter {
            it in 0..100
        }

        if (validos.isEmpty()) {
            return "Sin datos válidos"
        }

        return String.format(
            Locale.US,
            "Promedio: %.1f%% · Completadas: %d",
            validos.average(),
            validos.count { it == 100 }
        )
    }


    // =========================================================
    // EJEMPLO C - NULL SAFETY
    // =========================================================

    fun nombreVisible(
        nombreCompleto: String?,
        alias: String
    ): String =
        nombreCompleto
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: alias


    // =========================================================
    // RETO ADICIONAL - ORDENAMIENTO
    // =========================================================

    /*
     * Orden:
     * 1. Vencidas primero.
     * 2. Prioridad alta después.
     * 3. Menor número de días restantes.
     */

    fun ordenarActividades(
        actividades: List<ActividadFormativa>
    ): List<ActividadFormativa> =
        actividades.sortedWith(

            compareBy<ActividadFormativa> {
                estadoActividad(it) != EstadoActividad.VENCIDA
            }

                .thenByDescending {
                    it.prioridad == Prioridad.ALTA
                }

                .thenBy {
                    it.diasRestantes
                }
        )


    // =========================================================
    // RESUMEN GENERAL
    // =========================================================

    fun resumen(
        actividades: List<ActividadFormativa>
    ): String {

        if (actividades.isEmpty()) {
            return "Sin datos"
        }

        val promedio = promedioProgreso(actividades)

        val completadas = actividades.count {
            estadoActividad(it) == EstadoActividad.COMPLETADA
        }

        val urgentes = actividadesUrgentes(actividades).size

        return String.format(
            Locale.US,
            "Promedio: %.1f%% · Completadas: %d · Urgentes: %d",
            promedio,
            completadas,
            urgentes
        )
    }
}