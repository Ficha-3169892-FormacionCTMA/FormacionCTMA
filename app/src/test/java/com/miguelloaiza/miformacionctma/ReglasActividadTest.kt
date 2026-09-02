package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.data.repository.InMemoryActividadRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ReglasActividadTest {

    @Test
    fun HU17ConfirmarLaEliminacionDeUnaActividad() {
        val repository = InMemoryActividadRepository()

        val actividadAntes = repository.obtenerActividad(1L)

        assertNotNull(actividadAntes)

        val actividadEliminada = repository.eliminarActividad(1L)

        assertNotNull(actividadEliminada)
        assertEquals(1L, actividadEliminada?.id)

        val actividadDespues = repository.obtenerActividad(1L)

        assertNull(actividadDespues)
    }

    @Test
    fun HU18RestaurarUnaActividadEliminada() {
        val repository = InMemoryActividadRepository()

        val actividadOriginal = repository.obtenerActividad(1L)

        assertNotNull(actividadOriginal)

        val actividadEliminada = repository.eliminarActividad(1L)

        assertNotNull(actividadEliminada)

        repository.restaurarActividad(actividadEliminada!!)

        val actividadRestaurada = repository.obtenerActividad(1L)

        assertNotNull(actividadRestaurada)

        assertEquals(
            actividadOriginal?.id,
            actividadRestaurada?.id
        )

        assertEquals(
            actividadOriginal?.titulo,
            actividadRestaurada?.titulo
        )

        assertEquals(
            actividadOriginal?.descripcion,
            actividadRestaurada?.descripcion
        )

        assertEquals(
            actividadOriginal?.progreso,
            actividadRestaurada?.progreso
        )

        assertEquals(
            actividadOriginal?.diasRestantes,
            actividadRestaurada?.diasRestantes
        )

        assertEquals(
            actividadOriginal?.prioridad,
            actividadRestaurada?.prioridad
        )
    }
}