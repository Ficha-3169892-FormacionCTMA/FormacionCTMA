package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasActividadTest {

    // ============================================================
    // HU-06 - Validar los datos de una actividad
    // ============================================================

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
    fun progresoNegativoDebeGenerarError() {
        val errores = ReglasActividad.validarActividad(
            titulo = "Actividad de prueba",
            progreso = -10
        )

        assertTrue(
            errores.contains("El progreso debe estar entre 0 y 100")
        )
    }

    @Test
    fun datosValidosNoDebenGenerarErrores() {
        val errores = ReglasActividad.validarActividad(
            titulo = "Actividad válida",
            progreso = 50
        )

        assertTrue(
            errores.isEmpty()
        )
    }

    // ============================================================
    // HU-02 - Consultar el estado de una actividad
    // ============================================================

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
    fun actividadConProgresoCeroDebeSerPendiente() {
        val actividad = ActividadFormativa(
            id = 5L,
            titulo = "Actividad pendiente",
            descripcion = null,
            progreso = 0,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        )

        val resultado = ReglasActividad.estadoActividad(actividad)

        assertEquals(
            EstadoActividad.PENDIENTE,
            resultado
        )
    }

    @Test
    fun actividadConProgresoParcialDebeEstarEnProceso() {
        val actividad = ActividadFormativa(
            id = 6L,
            titulo = "Actividad en proceso",
            descripcion = null,
            progreso = 60,
            diasRestantes = 5,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.estadoActividad(actividad)

        assertEquals(
            EstadoActividad.EN_PROCESO,
            resultado
        )
    }

    // ============================================================
    // HU-03 - Consultar el progreso de las actividades
    // ============================================================

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
    fun promedioDebeCalcularseCorrectamente() {
        val actividades = listOf(
            ActividadFormativa(
                id = 13L,
                titulo = "Actividad 1",
                descripcion = null,
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            ),
            ActividadFormativa(
                id = 14L,
                titulo = "Actividad 2",
                descripcion = null,
                progreso = 100,
                diasRestantes = 5,
                prioridad = Prioridad.ALTA
            )
        )

        val resultado = ReglasActividad.promedioProgreso(actividades)

        assertEquals(
            75.0,
            resultado,
            0.0
        )
    }

    // ============================================================
    // HU-05 - Identificar actividades urgentes y prioridades
    // ============================================================

    @Test
    fun actividadConDosDiasRestantesDebeSerUrgente() {
        val actividad = ActividadFormativa(
            id = 7L,
            titulo = "Actividad urgente",
            descripcion = null,
            progreso = 50,
            diasRestantes = 2,
            prioridad = Prioridad.ALTA
        )

        val resultado = ReglasActividad.actividadesUrgentes(
            listOf(actividad)
        )

        assertEquals(
            1,
            resultado.size
        )
    }

    @Test
    fun actividadConUnDiaRestanteDebeSerUrgente() {
        val actividad = ActividadFormativa(
            id = 15L,
            titulo = "Actividad urgente",
            descripcion = null,
            progreso = 30,
            diasRestantes = 1,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.actividadesUrgentes(
            listOf(actividad)
        )

        assertTrue(
            resultado.contains(actividad)
        )
    }

    @Test
    fun actividadConMasDeDosDiasNoDebeSerUrgente() {
        val actividad = ActividadFormativa(
            id = 16L,
            titulo = "Actividad planificada",
            descripcion = null,
            progreso = 30,
            diasRestantes = 5,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.actividadesUrgentes(
            listOf(actividad)
        )

        assertTrue(
            resultado.isEmpty()
        )
    }

    @Test
    fun actividadCompletadaNoDebeSerUrgente() {
        val actividad = ActividadFormativa(
            id = 8L,
            titulo = "Actividad completada",
            descripcion = null,
            progreso = 100,
            diasRestantes = 0,
            prioridad = Prioridad.ALTA
        )

        val resultado = ReglasActividad.actividadesUrgentes(
            listOf(actividad)
        )

        assertTrue(
            resultado.isEmpty()
        )
    }

    @Test
    fun actividadUrgenteDebeTenerPrioridadUrgente() {
        val compromiso = ReglasActividad.Compromiso(
            titulo = "Entrega próxima",
            diasRestantes = 2,
            completado = false
        )

        val resultado = ReglasActividad.prioridad(compromiso)

        assertEquals(
            "Urgente",
            resultado
        )
    }

    @Test
    fun actividadCompletadaDebeTenerPrioridadFinalizado() {
        val compromiso = ReglasActividad.Compromiso(
            titulo = "Actividad terminada",
            diasRestantes = 0,
            completado = true
        )

        val resultado = ReglasActividad.prioridad(compromiso)

        assertEquals(
            "Finalizado",
            resultado
        )
    }

    @Test
    fun actividadVencidaDebeTenerPrioridadVencido() {
        val compromiso = ReglasActividad.Compromiso(
            titulo = "Actividad vencida",
            diasRestantes = -1,
            completado = false
        )

        val resultado = ReglasActividad.prioridad(compromiso)

        assertEquals(
            "Vencido",
            resultado
        )
    }

    // ============================================================
    // HU-07 - Buscar una actividad por título
    // ============================================================

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
    fun busquedaVaciaDebeDevolverListaVacia() {
        val actividades = listOf(
            ActividadFormativa(
                id = 9L,
                titulo = "Actividad de prueba",
                descripcion = null,
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            )
        )

        val resultado = ReglasActividad.buscarPorTitulo(
            actividades = actividades,
            texto = " "
        )

        assertTrue(
            resultado.isEmpty()
        )
    }

    @Test
    fun busquedaSinCoincidenciasDebeDevolverListaVacia() {
        val actividades = listOf(
            ActividadFormativa(
                id = 10L,
                titulo = "Actividad de Kotlin",
                descripcion = null,
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            )
        )

        val resultado = ReglasActividad.buscarPorTitulo(
            actividades = actividades,
            texto = "Java"
        )

        assertTrue(
            resultado.isEmpty()
        )
    }

    @Test
    fun busquedaParcialDebeEncontrarActividad() {
        val actividades = listOf(
            ActividadFormativa(
                id = 17L,
                titulo = "Desarrollo de aplicación móvil",
                descripcion = null,
                progreso = 50,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            )
        )

        val resultado = ReglasActividad.buscarPorTitulo(
            actividades = actividades,
            texto = "aplicación"
        )

        assertEquals(
            1,
            resultado.size
        )

        assertEquals(
            "Desarrollo de aplicación móvil",
            resultado.first().titulo
        )
    }

    // ============================================================
    // HU-08 - Ordenar actividades
    // ============================================================

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
    fun listaVaciaAlOrdenarDebeSeguirVacia() {
        val resultado = ReglasActividad.ordenarActividades(
            emptyList()
        )

        assertTrue(
            resultado.isEmpty()
        )
    }

    // ============================================================
    // HU-03 - Resumen del progreso
    // ============================================================

    @Test
    fun resumenDebeMostrarPromedioCompletadasYUrgentes() {
        val actividades = listOf(
            ActividadFormativa(
                id = 11L,
                titulo = "Actividad completada",
                descripcion = null,
                progreso = 100,
                diasRestantes = 5,
                prioridad = Prioridad.ALTA
            ),
            ActividadFormativa(
                id = 12L,
                titulo = "Actividad urgente",
                descripcion = null,
                progreso = 50,
                diasRestantes = 2,
                prioridad = Prioridad.MEDIA
            )
        )

        val resultado = ReglasActividad.resumen(actividades)

        assertTrue(
            resultado.contains("Promedio:")
        )

        assertTrue(
            resultado.contains("Completadas: 1")
        )

        assertTrue(
            resultado.contains("Urgentes: 1")
        )
    }

    @Test
    fun resumenDeListaVaciaDebeMostrarSinDatos() {
        val resultado = ReglasActividad.resumen(
            emptyList()
        )

        assertEquals(
            "Sin datos",
            resultado
        )
    }

    // ============================================================
    // Pruebas adicionales de resumenProgresos
    // ============================================================

    @Test
    fun resumenProgresosDeListaVaciaDebeMostrarSinDatos() {
        val resultado = ReglasActividad.resumenProgresos(
            emptyList()
        )

        assertEquals(
            "Sin datos",
            resultado
        )
    }

    @Test
    fun resumenProgresosDebeContarActividadesCompletadas() {
        val resultado = ReglasActividad.resumenProgresos(
            listOf(100, 50, 100)
        )

        assertTrue(
            resultado.contains("Completadas: 2")
        )
    }

    // ============================================================
    // Prueba de dato opcional
    // ============================================================

    @Test
    fun nombreVisibleDebeUsarNombreCompletoCuandoExiste() {
        val resultado = ReglasActividad.nombreVisible(
            nombreCompleto = " Juan Pérez ",
            alias = "Juan"
        )

        assertEquals(
            "Juan Pérez",
            resultado
        )
    }

    @Test
    fun nombreVisibleDebeUsarAliasCuandoNombreEstaVacio() {
        val resultado = ReglasActividad.nombreVisible(
            nombreCompleto = " ",
            alias = "Juan"
        )

        assertEquals(
            "Juan",
            resultado
        )
    }
}