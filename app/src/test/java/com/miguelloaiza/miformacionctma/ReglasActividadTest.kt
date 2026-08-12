package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasActividadTest {

    private fun actividad(
        titulo: String = "Actividad",
        progreso: Int,
        dias: Int,
        prioridad: Prioridad = Prioridad.MEDIA
    ) = ActividadFormativa(
        id = 1L,
        titulo = titulo,
        descripcion = null,
        progreso = progreso,
        diasRestantes = dias,
        prioridad = prioridad
    )

    @Test
    fun tituloVacioDevuelveError() {
        val errores = ReglasActividad.validarActividad(" ", 50)

        assertTrue(errores.contains("El título es obligatorio"))
    }

    @Test
    fun progreso120DevuelveError() {
        val errores = ReglasActividad.validarActividad(
            "Kotlin",
            120
        )

        assertTrue(
            errores.contains("El progreso debe estar entre 0 y 100")
        )
    }

    @Test
    fun validacionDevuelveTodosLosErrores() {
        val errores = ReglasActividad.validarActividad(" ", 120)

        assertEquals(2, errores.size)
    }

    @Test
    fun actividadVencida() {
        val resultado = ReglasActividad.estadoActividad(
            actividad(progreso = 80, dias = -1)
        )

        assertEquals(
            EstadoActividad.VENCIDA,
            resultado
        )
    }

    @Test
    fun actividadCompletaNoEsVencida() {
        val resultado = ReglasActividad.estadoActividad(
            actividad(progreso = 100, dias = -2)
        )

        assertEquals(
            EstadoActividad.COMPLETADA,
            resultado
        )
    }

    @Test
    fun listaVaciaControlaPromedio() {
        assertEquals(
            0.0,
            ReglasActividad.promedioProgreso(emptyList()),
            0.0
        )
    }

    @Test
    fun busquedaIgnoraMayusculasYEspacios() {
        val actividades = listOf(
            actividad(
                titulo = "Kotlin básico",
                progreso = 80,
                dias = 2
            )
        )

        val resultado = ReglasActividad.buscarPorTitulo(
            actividades,
            " kotlin "
        )

        assertEquals(1, resultado.size)
    }

    @Test
    fun completadaNoApareceComoUrgente() {
        val actividades = listOf(
            actividad(progreso = 80, dias = 2),
            actividad(progreso = 100, dias = 1)
        )

        assertEquals(
            1,
            ReglasActividad.actividadesUrgentes(actividades).size
        )
    }

    @Test
    fun nombreVisibleUsaAliasCuandoEsNull() {
        assertEquals(
            "Aprendiz",
            ReglasActividad.nombreVisible(null, "Aprendiz")
        )
    }

    @Test
    fun retoOrdenaVencidasPrimero() {
        val actividades = listOf(
            actividad(
                titulo = "Normal",
                progreso = 40,
                dias = 5
            ),
            actividad(
                titulo = "Vencida",
                progreso = 40,
                dias = -1
            )
        )

        val ordenadas = ReglasActividad.ordenarActividades(
            actividades
        )

        assertEquals(
            "Vencida",
            ordenadas.first().titulo
        )
    }
}
