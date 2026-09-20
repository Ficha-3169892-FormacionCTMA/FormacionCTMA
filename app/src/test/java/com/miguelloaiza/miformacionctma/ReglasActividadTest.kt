package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Test
import org.junit.Assert.*

class ReglasActividadTest {

    @Test
    fun tituloVacioDebeGenerarError() {
        val errores = ReglasActividad.validarActividad(
            titulo = " ",
            progreso = 50
        )
        assertTrue(errores.contains("El título es obligatorio"))
    }

    @Test
    fun progresoMayorQue100DebeGenerarError() {
        val errores = ReglasActividad.validarActividad(
            titulo = "Actividad de prueba",
            progreso = 120
        )
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100"))
    }

    @Test
    fun actividadConDiasNegativosDebeSerVencida() {
        val actividad = ActividadFormativa(
            id = 1L, titulo = "Actividad vencida", descripcion = null,
            progreso = 80, diasRestantes = -1, prioridad = Prioridad.ALTA
        )
        val resultado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.VENCIDA, resultado)
    }

    @Test
    fun actividadCon100PorcientoDebeSerCompletadaAunqueEsteVencida() {
        val actividad = ActividadFormativa(
            id = 2L, titulo = "Actividad completada", descripcion = null,
            progreso = 100, diasRestantes = -2, prioridad = Prioridad.MEDIA
        )
        val resultado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.COMPLETADA, resultado)
    }

    @Test
    fun listaVaciaDebeDevolverPromedioCero() {
        val actividades = emptyList<ActividadFormativa>()
        val resultado = ReglasActividad.promedioProgreso(actividades)
        assertEquals(0.0, resultado, 0.0)
    }

    @Test
    fun busquedaDebeIgnorarMayusculasYEspacios() {
        val actividades = listOf(
            ActividadFormativa(3L, "Kotlin básico", "Desc", 50, 5, Prioridad.BAJA),
            ActividadFormativa(4L, "Android Studio", null, 80, 3, Prioridad.MEDIA)
        )
        val resultado = ReglasActividad.buscarPorTitulo(actividades, " kotlin ")
        assertEquals(1, resultado.size)
        assertEquals("Kotlin básico", resultado.first().titulo)
    }

    @Test
    fun ordenarActividadesDebePriorizarVencidasAltaYMenosDias() {
        val actividades = listOf(
            ActividadFormativa(1L, "Actividad normal", null, 40, 5, Prioridad.BAJA),
            ActividadFormativa(2L, "Actividad urgente", null, 50, 2, Prioridad.ALTA),
            ActividadFormativa(3L, "Actividad vencida", null, 80, -1, Prioridad.MEDIA),
            ActividadFormativa(4L, "Actividad alta cercana", null, 30, 1, Prioridad.ALTA)
        )
        val resultado = ReglasActividad.ordenarActividades(actividades)
        assertEquals(listOf(3L, 4L, 2L, 1L), resultado.map { it.id })
    }

    // --- PRUEBAS DE HISTORIAS DE USUARIO ---

    @Test
    fun `HU-15 Escenario 1 - Cambiar prioridad correctamente`() {
        val actividad = ActividadFormativa(1L, "Test", null, 40, 5, Prioridad.BAJA)
        val resultado = ReglasActividad.cambiarPrioridad(actividad, Prioridad.ALTA)
        assertEquals(Prioridad.ALTA, resultado.prioridad)
    }

    @Test
    fun `HU-15 Escenario 2 - Asignar la misma prioridad`() {
        val actividad = ActividadFormativa(2L, "Test", null, 60, 3, Prioridad.MEDIA)
        val resultado = ReglasActividad.cambiarPrioridad(actividad, Prioridad.MEDIA)
        assertEquals(Prioridad.MEDIA, resultado.prioridad)
        assertEquals(actividad, resultado)
    }

    @Test
    fun `HU-16 Escenario 1 - Conteo correcto por estado`() {
        val actividades = listOf(
            ActividadFormativa(1, "Vencida", null, 50, -1, Prioridad.MEDIA),
            ActividadFormativa(2, "En progreso", null, 50, 5, Prioridad.MEDIA),
            ActividadFormativa(3, "Completada", null, 100, 5, Prioridad.MEDIA),
            ActividadFormativa(4, "Otra en progreso", null, 20, 3, Prioridad.MEDIA)
        )
        val conteo = ReglasActividad.contarPorEstado(actividades)
        assertEquals(1, conteo[EstadoActividad.VENCIDA] ?: 0)
        assertEquals(2, conteo[EstadoActividad.EN_PROCESO] ?: 0)
        assertEquals(1, conteo[EstadoActividad.COMPLETADA] ?: 0)
    }

    @Test
    fun `HU-19 Escenario 1 - Filtrar por prioridad ALTA`() {
        val actividades = listOf(
            ActividadFormativa(1, "A", null, 0, 5, Prioridad.ALTA),
            ActividadFormativa(2, "B", null, 0, 5, Prioridad.BAJA)
        )
        val resultado = ReglasActividad.filtrarPorPrioridad(actividades, Prioridad.ALTA)
        assertEquals(1, resultado.size)
        assertEquals(Prioridad.ALTA, resultado.first().prioridad)
    }

    @Test
    fun `HU-20 Escenario 1 - Mensaje para lista vacia`() {
        val mensaje = ReglasActividad.obtenerMensajeVacio(totalRegistradas = 0, totalFiltradas = 0)
        assertEquals("Aún no tienes actividades", mensaje)
    }

    @Test
    fun `HU-21 Escenario 1 - Duplicar correctamente`() {
        val original = ActividadFormativa(1, "Original", "Desc", 80, 5, Prioridad.ALTA)
        val duplicada = ReglasActividad.duplicarActividad(original, nuevoId = 10L)
        assertEquals(10L, duplicada.id)
        assertEquals(0, duplicada.progreso)
        assertEquals(EstadoActividad.PENDIENTE, ReglasActividad.estadoActividad(duplicada))
    }
}
