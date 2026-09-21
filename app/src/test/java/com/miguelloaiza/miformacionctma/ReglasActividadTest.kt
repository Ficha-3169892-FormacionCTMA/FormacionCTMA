package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Test
import org.junit.Assert.*

class ReglasActividadTest {

    // HU-01 – pantalla Debe Mostrar Titulo Y Actividades

    @Test
    fun `CP-01 - Verificar que la lista de actividades se procese correctamente`() {
        // Dado que existen actividades registradas
        val actividades = listOf(
            ActividadFormativa(1, "Actividad 1", "Desc", 50, 5, Prioridad.ALTA)
        )
        // Cuando se busca por titulo (logica de filtrado)
        val resultado = ReglasActividad.buscarPorTitulo(actividades, "Actividad 1")
        // Entonces la lista no debe estar vacia
        assertTrue(resultado.isNotEmpty())
        assertEquals("Actividad 1", resultado[0].titulo)
    }

    @Test
    fun `CP-02 - Verificar que cada actividad contenga los campos obligatorios`() {
        // Dado una actividad con todos sus campos
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Prototipo",
            descripcion = "Evidencia de prototipo",
            progreso = 0,
            diasRestantes = 10,
            prioridad = Prioridad.MEDIA
        )
        // Entonces los campos deben ser accesibles y correctos
        assertEquals("Prototipo", actividad.titulo)
        assertEquals("Evidencia de prototipo", actividad.descripcion)
        assertEquals(0, actividad.progreso)
        assertEquals(10, actividad.diasRestantes)
        assertEquals(Prioridad.MEDIA, actividad.prioridad)
    }

    @Test
    fun `CP-03 - Verificar el comportamiento cuando no existen actividades`() {
        // Dado una lista vacia
        val actividades = emptyList<ActividadFormativa>()
        // Cuando se genera el resumen logico
        val resumen = ReglasActividad.resumen(actividades)
        // Entonces debe indicar que no hay datos
        assertEquals("Sin datos", resumen)
    }

    // HU-02 – actividad Con Dias Negativos Debe Ser Vencida

    @Test
    fun `CP-04 - Actividad con 0 por ciento de progreso debe ser Pendiente`() {
        val actividad = ActividadFormativa(1, "T1", null, 0, 5, Prioridad.MEDIA)
        val estado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.PENDIENTE, estado)
    }

    @Test
    fun `CP-05 - Actividad con progreso entre 1 y 99 por ciento debe ser En proceso`() {
        val actividad = ActividadFormativa(1, "T1", null, 50, 5, Prioridad.MEDIA)
        val estado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.EN_PROCESO, estado)
    }

    @Test
    fun `CP-06 - Actividad con 100 por ciento de progreso debe ser Completada`() {
        val actividad = ActividadFormativa(1, "T1", null, 100, 5, Prioridad.MEDIA)
        val estado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.COMPLETADA, estado)
    }

    @Test
    fun `CP-07 - Actividad con dias restantes negativos y progreso inferior al 100 por ciento debe ser Vencida`() {
        val actividad = ActividadFormativa(1, "T1", null, 50, -1, Prioridad.MEDIA)
        val estado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.VENCIDA, estado)
    }

    @Test
    fun `CP-08 - Actividad con 100 por ciento de progreso permanece Completada aunque tenga dias restantes negativos`() {
        val actividad = ActividadFormativa(1, "T1", null, 100, -2, Prioridad.MEDIA)
        val estado = ReglasActividad.estadoActividad(actividad)
        assertEquals(EstadoActividad.COMPLETADA, estado)
    }
}
