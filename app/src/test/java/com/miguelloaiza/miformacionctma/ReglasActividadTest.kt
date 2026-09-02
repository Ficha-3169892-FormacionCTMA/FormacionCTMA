package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasActividadTest {

    @Test
    fun tituloVacioDebeGenerarError() {
        val errores = ReglasActividad.validarActividad(
            titulo = " ",
            progreso = 50
        )

        assertTrue(
            errores.contains("El título es obligatorio")
        )
    }

    @Test
    fun progresoMayorQue100DebeGenerarError() {
        val errores = ReglasActividad.validarActividad(
            titulo = "Actividad de prueba",
            progreso = 120
        )

        assertTrue(
            errores.contains("El progreso debe estar entre 0 y 100")
        )
    }

    @Test
    fun actividadConDiasNegativosDebeSerVencida() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Actividad vencida",
            descripcion = null,
            progreso = 80,
            diasRestantes = -1,
            prioridad = Prioridad.ALTA
        )

        val resultado = ReglasActividad.estadoActividad(actividad)

        assertEquals(
            EstadoActividad.VENCIDA,
            resultado
        )
    }

    @Test
    fun actividadCon100PorcientoDebeSerCompletadaAunqueEsteVencida() {
        val actividad = ActividadFormativa(
            id = 2L,
            titulo = "Actividad completada",
            descripcion = null,
            progreso = 100,
            diasRestantes = -2,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.estadoActividad(actividad)

        assertEquals(
            EstadoActividad.COMPLETADA,
            resultado
        )
    }

    @Test
    fun listaVaciaDebeDevolverPromedioCero() {
        val actividades = emptyList<ActividadFormativa>()

        val resultado = ReglasActividad.promedioProgreso(actividades)

        assertEquals(
            0.0,
            resultado,
            0.0
        )
    }

    @Test
    fun busquedaDebeIgnorarMayusculasYEspacios() {
        val actividades = listOf(
            ActividadFormativa(
                id = 3L,
                titulo = "Kotlin básico",
                descripcion = "Introducción a Kotlin",
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.BAJA
            ),
            ActividadFormativa(
                id = 4L,
                titulo = "Android Studio",
                descripcion = null,
                progreso = 80,
                diasRestantes = 3,
                prioridad = Prioridad.MEDIA
            )
        )

        val resultado = ReglasActividad.buscarPorTitulo(
            actividades = actividades,
            texto = " kotlin "
        )

        assertEquals(
            1,
            resultado.size
        )

        assertEquals(
            "Kotlin básico",
            resultado.first().titulo
        )
    }

    @Test
    fun ordenarActividadesDebePriorizarVencidasAltaYMenosDias() {
        val actividades = listOf(
            ActividadFormativa(
                id = 1L,
                titulo = "Actividad normal",
                descripcion = null,
                progreso = 40,
                diasRestantes = 5,
                prioridad = Prioridad.BAJA
            ),
            ActividadFormativa(
                id = 2L,
                titulo = "Actividad urgente",
                descripcion = null,
                progreso = 50,
                diasRestantes = 2,
                prioridad = Prioridad.ALTA
            ),
            ActividadFormativa(
                id = 3L,
                titulo = "Actividad vencida",
                descripcion = null,
                progreso = 80,
                diasRestantes = -1,
                prioridad = Prioridad.MEDIA
            ),
            ActividadFormativa(
                id = 4L,
                titulo = "Actividad alta cercana",
                descripcion = null,
                progreso = 30,
                diasRestantes = 1,
                prioridad = Prioridad.ALTA
            )
        )

        val resultado = ReglasActividad.ordenarActividades(actividades)

        assertEquals(
            listOf(3L, 4L, 2L, 1L),
            resultado.map { it.id }
        )
    }

    @Test
    fun cambiarPrioridadDebeActualizarElCampoPrioridad() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Actividad de prueba",
            descripcion = null,
            progreso = 40,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        )

        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.ALTA
        )

        assertEquals(
            Prioridad.ALTA,
            resultado.prioridad
        )
    }

    @Test
    fun cambiarPrioridadALaMismaQueYaTieneNoDebeAlterarOtrosCampos() {
        val actividad = ActividadFormativa(
            id = 2L,
            titulo = "Actividad media",
            descripcion = "Sin cambios esperados",
            progreso = 60,
            diasRestantes = 3,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.MEDIA
        )

        assertEquals(
            Prioridad.MEDIA,
            resultado.prioridad
        )

        assertEquals(
            actividad.titulo,
            resultado.titulo
        )

        assertEquals(
            actividad.progreso,
            resultado.progreso
        )
    }
}