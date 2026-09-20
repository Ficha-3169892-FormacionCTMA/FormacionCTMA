package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Test
import org.junit.Assert.*

class ReglasActividadTest {

    // HU-15 – Editar la prioridad de una actividad

    @Test
    fun `HU-15 Escenario 1 - Cambiar prioridad correctamente`() {
        // Dado que existe una actividad con prioridad BAJA
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Actividad de prueba",
            descripcion = null,
            progreso = 40,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        )

        // Cuando cambio su prioridad a ALTA
        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.ALTA
        )

        // Entonces la actividad debe reflejar la nueva prioridad
        assertEquals(Prioridad.ALTA, resultado.prioridad)
        assertEquals(actividad.id, resultado.id)
        assertEquals(actividad.titulo, resultado.titulo)
    }

    @Test
    fun `HU-15 Escenario 2 - Asignar la misma prioridad que ya tiene`() {
        // Dado que una actividad ya tiene prioridad MEDIA
        val actividad = ActividadFormativa(
            id = 2L,
            titulo = "Actividad media",
            descripcion = "Sin cambios esperados",
            progreso = 60,
            diasRestantes = 3,
            prioridad = Prioridad.MEDIA
        )

        // Cuando intento cambiarla nuevamente a MEDIA
        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.MEDIA
        )

        // Entonces no debe generar error y la actividad debe mantener su prioridad sin duplicarse
        assertEquals(Prioridad.MEDIA, resultado.prioridad)
        assertEquals(actividad, resultado) // Al usar .copy() con el mismo valor, el objeto resultante es estructuralmente igual
    }

    // HU-16 – Calcular el total de actividades por estado

    @Test
    fun `HU-16 Escenario 1 - Conteo correcto`() {
        // Dado que existen actividades en distintos estados
        val actividades = listOf(
            ActividadFormativa(1, "Vencida", null, 50, -1, Prioridad.MEDIA), // VENCIDA (diasRestantes < 0)
            ActividadFormativa(2, "En progreso", null, 50, 5, Prioridad.MEDIA), // EN_PROCESO (0 < progreso < 100)
            ActividadFormativa(3, "Completada", null, 100, 5, Prioridad.MEDIA), // COMPLETADA (progreso == 100)
            ActividadFormativa(4, "Otra en progreso", null, 20, 3, Prioridad.MEDIA) // EN_PROCESO
        )

        // Cuando solicito el resumen por estado
        val conteo = ReglasActividad.contarPorEstado(actividades)

        // Entonces debe devolverse el número correcto de actividades para cada estado
        assertEquals(1, conteo[EstadoActividad.VENCIDA] ?: 0)
        assertEquals(2, conteo[EstadoActividad.EN_PROCESO] ?: 0)
        assertEquals(1, conteo[EstadoActividad.COMPLETADA] ?: 0)
        assertEquals(0, conteo[EstadoActividad.PENDIENTE] ?: 0)
    }

    @Test
    fun `HU-16 Escenario 2 - Lista vacia`() {
        // Dado que no hay actividades registradas
        val actividades = emptyList<ActividadFormativa>()

        // Cuando solicito el resumen por estado
        val conteo = ReglasActividad.contarPorEstado(actividades)

        // Entonces todos los conteos deben ser cero
        assertTrue(conteo.isEmpty())
        assertEquals(0, conteo[EstadoActividad.VENCIDA] ?: 0)
        assertEquals(0, conteo[EstadoActividad.EN_PROCESO] ?: 0)
        assertEquals(0, conteo[EstadoActividad.COMPLETADA] ?: 0)
        assertEquals(0, conteo[EstadoActividad.PENDIENTE] ?: 0)
    }
}
